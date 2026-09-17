package xyz.superbet.supercoctails.presentation.state

import xyz.superbet.supercoctails.domain.model.Cocktail

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Content(val cocktail: Cocktail) : DetailUiState()
    object Error : DetailUiState()
}