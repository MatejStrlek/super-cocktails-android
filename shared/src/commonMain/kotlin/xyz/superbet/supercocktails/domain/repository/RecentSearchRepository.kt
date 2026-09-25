package xyz.superbet.supercocktails.domain.repository

import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    fun getRecentSearches(): Flow<List<String>>
    suspend fun addRecentSearch(term: String)
    suspend fun deleteRecentSearch(term: String)
}
