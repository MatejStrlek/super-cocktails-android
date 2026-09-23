package xyz.superbet.supercoctails.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import xyz.superbet.supercoctails.domain.repository.RecentSearchRepository

private val KEY_RECENT_SEARCHES = stringPreferencesKey("recent_searches")
private const val MAX_RECENT_SEARCHES = 5

class RecentSearchRepositoryImpl(private val context: Context) : RecentSearchRepository {
    override fun getRecentSearches(): Flow<List<String>> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_RECENT_SEARCHES]
                ?.let { Json.decodeFromString<List<String>>(it) }
                ?: emptyList()
        }

    override suspend fun addRecentSearch(term: String) {
        if (term.isBlank()) return
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_RECENT_SEARCHES]
                ?.let { Json.decodeFromString<List<String>>(it) }
                ?.toMutableList()
                ?: mutableListOf()
            current.remove(term)
            current.add(0, term)
            if (current.size > MAX_RECENT_SEARCHES) current.removeAt(current.lastIndex)
            prefs[KEY_RECENT_SEARCHES] = Json.encodeToString(current)
        }
    }

    override suspend fun deleteRecentSearch(term: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_RECENT_SEARCHES]
                ?.let { Json.decodeFromString<List<String>>(it) }
                ?.toMutableList()
                ?: return@edit
            current.remove(term)
            prefs[KEY_RECENT_SEARCHES] = Json.encodeToString(current)
        }
    }
}