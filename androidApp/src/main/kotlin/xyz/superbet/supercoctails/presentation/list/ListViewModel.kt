package xyz.superbet.supercoctails.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.domain.usecase.AddRecentSearchUseCase
import xyz.superbet.supercoctails.domain.usecase.DeleteRecentSearchUseCase
import xyz.superbet.supercoctails.domain.usecase.GetRecentSearchesUseCase
import xyz.superbet.supercoctails.domain.usecase.GetRecommendedCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.SearchCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.ToggleFavoriteUseCase
import xyz.superbet.supercoctails.presentation.state.ListUiState
import kotlin.time.Duration.Companion.milliseconds

class ListViewModel(
    private val searchCocktailsUseCase: SearchCocktailsUseCase,
    private val getRecommendedCocktailsUseCase: GetRecommendedCocktailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val addRecentSearchUseCase: AddRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _isSearchFocused = MutableStateFlow(false)
    val isSearchFocused: StateFlow<Boolean> = _isSearchFocused.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<ListUiState> = combine(
        _searchQuery,
        _isSearchFocused,
        getRecentSearchesUseCase()
    ) { query, focused, recents -> Triple(query, focused, recents) }
        .flatMapLatest { (query, focused, recents) ->
            when {
                // focused with blank query — show recent searches immediately, no debounce
                focused && query.isBlank() ->
                    flow { emit(ListUiState.SearchFocused(recents)) }
                // blank query, not focused — show recommended
                query.isBlank() ->
                    getRecommendedCocktailsUseCase().map { cocktails ->
                        if (cocktails.isEmpty()) ListUiState.Loading else ListUiState.Content(
                            cocktails
                        )
                    }
                // active search — debounce before hitting Room/network
                else ->
                    flow { emit(query) }
                        .debounce(300.milliseconds)
                        .flatMapLatest { q ->
                            searchCocktailsUseCase(q)
                                .map { cocktails ->
                                    if (cocktails.isEmpty()) ListUiState.Empty
                                    else ListUiState.Content(cocktails)
                                }
                                .onStart { emit(ListUiState.Loading) }
                                .catch { e ->
                                    emit(
                                        ListUiState.Error(
                                            e.message ?: "Something went wrong"
                                        )
                                    )
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
}