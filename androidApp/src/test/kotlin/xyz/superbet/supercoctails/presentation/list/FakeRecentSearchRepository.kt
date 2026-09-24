package xyz.superbet.supercoctails.presentation.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.superbet.supercoctails.domain.repository.RecentSearchRepository

class FakeRecentSearchRepository : RecentSearchRepository {
    private val searches = MutableStateFlow<List<String>>(emptyList())

    override fun getRecentSearches(): Flow<List<String>> = searches

    override suspend fun addRecentSearch(term: String) {
        val updated = (listOf(term) + searches.value.filter { it != term }).take(5)
        searches.value = updated
    }

    override suspend fun deleteRecentSearch(term: String) {
        searches.value = searches.value.filter { it != term }
    }
}