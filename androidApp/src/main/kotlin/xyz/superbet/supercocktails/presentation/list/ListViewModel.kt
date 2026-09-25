package xyz.superbet.supercocktails.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.superbet.supercocktails.domain.model.ThemePreference
import xyz.superbet.supercocktails.domain.usecase.cocktail.GetRecommendedCocktailsUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.SearchCocktailsUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.ToggleFavoriteUseCase
import xyz.superbet.supercocktails.domain.usecase.search.AddRecentSearchUseCase
import xyz.superbet.supercocktails.domain.usecase.search.DeleteRecentSearchUseCase
import xyz.superbet.supercocktails.domain.usecase.search.GetRecentSearchesUseCase
import xyz.superbet.supercocktails.domain.usecase.theme.GetThemePreferenceUseCase
import xyz.superbet.supercocktails.domain.usecase.theme.SetThemePreferenceUseCase
import xyz.superbet.supercocktails.presentation.state.ListUiState
import kotlin.time.Duration.Companion.milliseconds

class ListViewModel(
    private val searchCocktailsUseCase: SearchCocktailsUseCase,
    private val getRecommendedCocktailsUseCase: GetRecommendedCocktailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val addRecentSearchUseCase: AddRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    getThemePreferenceUseCase: GetThemePreferenceUseCase,
    private val setThemePreferenceUseCase: SetThemePreferenceUseCase,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _isSearchFocused = MutableStateFlow(false)
    val isSearchFocused: StateFlow<Boolean> = _isSearchFocused.asStateFlow()
    private val _retryTrigger = MutableStateFlow(0)
    private val _searchErrorEvent = Channel<String>(Channel.BUFFERED)
    val searchErrorEvent = _searchErrorEvent.receiveAsFlow()

    val themePreference: StateFlow<ThemePreference> = getThemePreferenceUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemePreference.SYSTEM)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ListUiState> = combine(
        _searchQuery,
        _isSearchFocused,
        _retryTrigger,
        getRecentSearchesUseCase()
    ) { query, focused, _, recents -> Triple(query, focused, recents) }
        .flatMapLatest { (query, focused, recents) ->
            when {
                // focused with blank query — show recent searches immediately, no debounce
                focused && query.isBlank() ->
                    flowOf(ListUiState.SearchFocused(recents))
                // blank query, not focused — show recommended
                query.isBlank() ->
                    getRecommendedCocktailsUseCase().map { cocktails ->
                        if (cocktails.isEmpty()) ListUiState.Loading else ListUiState.Content(
                            cocktails
                        )
                    }.catch { e ->
                        val msg = e.message ?: "Something went wrong"
                        _searchErrorEvent.trySend(msg)
                        emit(ListUiState.Error(msg))
                    }
                // active search — debounce before hitting Room/network
                else ->
                    flowOf(query)
                        .debounce(300.milliseconds)
                        .flatMapLatest { q ->
                            searchCocktailsUseCase(q)
                                .map { cocktails ->
                                    if (cocktails.isEmpty()) ListUiState.Empty
                                    else ListUiState.Content(cocktails)
                                }
                                .onStart { emit(ListUiState.Loading) }
                                .catch { e ->
                                    val msg = e.message ?: "Something went wrong"
                                    _searchErrorEvent.trySend(msg)
                                    emit(ListUiState.Error(msg))
                                }
                        }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState.Loading)

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchFocused() {
        _isSearchFocused.value = true
    }

    fun onSearchUnfocused() {
        _isSearchFocused.value = false
    }

    fun onSearchSubmitted() {
        saveCurrentQueryAsRecent()
    }

    fun onCocktailClicked() {
        saveCurrentQueryAsRecent()
    }

    fun deleteRecentSearch(term: String) {
        viewModelScope.launch { deleteRecentSearchUseCase(term) }
    }

    private fun saveCurrentQueryAsRecent() {
        val term = _searchQuery.value.trim()
        if (term.isNotBlank()) {
            viewModelScope.launch { addRecentSearchUseCase(term) }
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch { toggleFavoriteUseCase(id) }
    }

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch { setThemePreferenceUseCase(theme) }
    }

    fun retry() {
        _retryTrigger.update { it + 1 }
    }
}