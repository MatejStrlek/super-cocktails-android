package xyz.superbet.supercoctails.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.domain.usecase.cocktail.GetCocktailByIdUseCase
import xyz.superbet.supercoctails.domain.usecase.cocktail.ToggleFavoriteUseCase
import xyz.superbet.supercoctails.presentation.state.DetailUiState

class DetailViewModel(
    private val getCocktailByIdUseCase: GetCocktailByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            try {
                val cocktail = getCocktailByIdUseCase(id)
                _uiState.value = if (cocktail != null) DetailUiState.Content(cocktail) else DetailUiState.Error
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = DetailUiState.Error
            }
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
            // reload so the star icon reflects the new state immediately
            load(id)
        }
    }
}