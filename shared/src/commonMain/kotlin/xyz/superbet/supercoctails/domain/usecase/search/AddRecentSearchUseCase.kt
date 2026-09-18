package xyz.superbet.supercoctails.domain.usecase.search

import xyz.superbet.supercoctails.domain.repository.RecentSearchRepository

class AddRecentSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(term: String) = repository.addRecentSearch(term)
}
