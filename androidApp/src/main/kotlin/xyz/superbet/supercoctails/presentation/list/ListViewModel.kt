package xyz.superbet.supercoctails.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.domain.usecase.GetRecommendedCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.SearchCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.ToggleFavoriteUseCase
import xyz.superbet.supercoctails.presentation.state.ListUiState
import kotlin.time.Duration.Companion.milliseconds

class ListViewModel(
    private val searchCocktailsUseCase: SearchCocktailsUseCase,
    private val getRecommendedCocktailsUseCase: GetRecommendedCocktailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<ListUiState> = _searchQuery
        .debounce(300.milliseconds)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getRecommendedCocktailsUseCase().map { cocktails ->
                    if (cocktails.isEmpty()) ListUiState.Loading else ListUiState.Content(cocktails)
                }
            } else {
                searchCocktailsUseCase(query)
                    .map { cocktails ->
                        if (cocktails.isEmpty()) ListUiState.Empty else ListUiState.Content(cocktails)
                    }
                    .onStart { emit(ListUiState.Loading) }
                    .catch { e -> emit(ListUiState.Error(e.message ?: "Something went wrong")) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState.Loading)

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch { toggleFavoriteUseCase(id) }
    }
}
