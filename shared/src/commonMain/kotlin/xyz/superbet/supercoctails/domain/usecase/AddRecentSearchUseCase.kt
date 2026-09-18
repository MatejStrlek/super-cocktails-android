package xyz.superbet.supercoctails.domain.usecase

import xyz.superbet.supercoctails.domain.repository.RecentSearchRepository

class AddRecentSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(term: String) = repository.addRecentSearch(term)
}
