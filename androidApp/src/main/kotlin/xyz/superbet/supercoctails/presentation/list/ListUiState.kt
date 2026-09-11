package xyz.superbet.supercoctails.presentation.list

import xyz.superbet.supercoctails.data.model.Cocktail

sealed class ListUiState {
    object Loading : ListUiState()
    data class Content(val cocktails: List<Cocktail>) : ListUiState()
}