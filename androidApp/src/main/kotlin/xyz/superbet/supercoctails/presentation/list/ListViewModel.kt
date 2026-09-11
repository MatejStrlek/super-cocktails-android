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
        viewModelScope.launch {
            val result = searchCocktailsUseCase("margarita")
            _uiState.value = ListUiState.Content(result)
        }
    }
}