package xyz.superbet.supercoctails.presentation.list

import xyz.superbet.supercoctails.domain.model.Cocktail

sealed class ListUiState {
    object Loading : ListUiState()
    data class Content(val cocktails: List<Cocktail>) : ListUiState()
    object Empty : ListUiState()
    data class Error(val message: String) : ListUiState()
}