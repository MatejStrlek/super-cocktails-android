package xyz.superbet.supercocktails.domain.usecase.search

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercocktails.domain.repository.RecentSearchRepository

class GetRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    operator fun invoke(): Flow<List<String>> = repository.getRecentSearches()
}
