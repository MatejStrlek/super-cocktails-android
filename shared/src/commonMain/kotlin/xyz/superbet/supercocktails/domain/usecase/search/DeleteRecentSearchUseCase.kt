package xyz.superbet.supercocktails.domain.usecase.search

import xyz.superbet.supercocktails.domain.repository.RecentSearchRepository

class DeleteRecentSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(term: String) = repository.deleteRecentSearch(term)
}
