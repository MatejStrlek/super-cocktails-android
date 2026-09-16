package xyz.superbet.supercoctails.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.usecase.GetRecommendedCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.SearchCocktailsUseCase
import xyz.superbet.supercoctails.presentation.state.ListUiState
import kotlin.time.Duration.Companion.milliseconds

class ListViewModel(
    private val searchCocktailsUseCase: SearchCocktailsUseCase,
    private val getRecommendedCocktailsUseCase: GetRecommendedCocktailsUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private var cachedRecommendedCocktails: List<Cocktail>? = null

    init {
        viewModelScope.launch {
            try {
                cachedRecommendedCocktails = getRecommendedCocktailsUseCase()
                if (searchQuery.value.isBlank()) {
                    searchQuery.value = ""
                }
            } catch (_: Exception) { }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<ListUiState> = searchQuery
        .debounce(300.milliseconds)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flow {
                    val recommended = cachedRecommendedCocktails
                    if (recommended == null) {
                        emit(ListUiState.Loading)
                    } else {
                        emit(if (recommended.isEmpty()) ListUiState.Empty else ListUiState.Content(recommended))
                    }
                }
            } else {
                flow {
                    emit(ListUiState.Loading)
                    try {
                        val cocktails = searchCocktailsUseCase(query)
                        emit(if (cocktails.isEmpty()) ListUiState.Empty else ListUiState.Content(cocktails))
                    } catch (e: Exception) {
                        emit(ListUiState.Error(e.message ?: "Something went wrong"))
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState.Loading)

    fun onQueryChanged(query: String) {
        searchQuery.value = query
    }
}