package xyz.superbet.supercoctails.presentation.state

import xyz.superbet.supercoctails.domain.model.Cocktail

sealed class ListUiState {
    object Loading : ListUiState()
    data class SearchFocused(val recentSearches: List<String>) : ListUiState()
    data class Content(val cocktails: List<Cocktail>) : ListUiState()
    object Empty : ListUiState()
    data class Error(val message: String) : ListUiState()
}