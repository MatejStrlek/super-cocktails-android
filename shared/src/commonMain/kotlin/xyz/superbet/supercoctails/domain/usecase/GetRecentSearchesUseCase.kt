package xyz.superbet.supercoctails.domain.usecase

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercoctails.domain.repository.RecentSearchRepository

class GetRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    operator fun invoke(): Flow<List<String>> = repository.getRecentSearches()
}
