package xyz.superbet.supercoctails.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.domain.usecase.GetRecommendedCocktailsUseCase
import xyz.superbet.supercoctails.domain.usecase.SearchCocktailsUseCase

class ListViewModel(
    private val searchCocktailsUseCase: SearchCocktailsUseCase,
    private val getRecommendedCocktailsUseCase: GetRecommendedCocktailsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        loadRecommended()
    }

    private fun loadRecommended() {
        _uiState.value = ListUiState.Loading
        viewModelScope.launch {
            try {
                val cocktails = getRecommendedCocktailsUseCase()
                _uiState.value = if (cocktails.isEmpty()) ListUiState.Empty
                                 else ListUiState.Content(cocktails)
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun search(query: String) {
        _uiState.value = ListUiState.Loading
        viewModelScope.launch {
            try {
                val cocktails = searchCocktailsUseCase(query)
                if (cocktails.isEmpty()) {
                    _uiState.value = ListUiState.Empty
                } else {
                    _uiState.value = ListUiState.Content(cocktails)
                }
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}