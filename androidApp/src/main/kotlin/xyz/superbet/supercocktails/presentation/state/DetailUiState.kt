package xyz.superbet.supercocktails.presentation.state

import xyz.superbet.supercocktails.domain.model.Cocktail

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Content(val cocktail: Cocktail) : DetailUiState()
    object Error : DetailUiState()
}