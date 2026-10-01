package xyz.superbet.supercocktails.presentation.state

import xyz.superbet.supercocktails.domain.model.Cocktail

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Content(val cocktail: Cocktail) : DetailUiState
    data object Error : DetailUiState
}