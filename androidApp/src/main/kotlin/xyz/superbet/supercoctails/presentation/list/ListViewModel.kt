package xyz.superbet.supercoctails.presentation.list

import androidx.lifecycle.ViewModel
import xyz.superbet.supercoctails.domain.usecase.SearchCocktailsUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel (
    private val searchCocktailsUseCase: SearchCocktailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        search("margarita")
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