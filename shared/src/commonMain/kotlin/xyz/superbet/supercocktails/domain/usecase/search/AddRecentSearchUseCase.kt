package xyz.superbet.supercocktails.domain.usecase.search

import xyz.superbet.supercocktails.domain.repository.RecentSearchRepository

class AddRecentSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(term: String) = repository.addRecentSearch(term)
}
