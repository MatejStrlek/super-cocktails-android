package xyz.superbet.supercocktails.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xyz.superbet.supercocktails.domain.model.ThemePreference
import xyz.superbet.supercocktails.domain.repository.ThemeRepository

private val KEY_THEME = stringPreferencesKey("theme_preference")

class ThemeRepositoryImpl(private val context: Context) : ThemeRepository {
    override fun getThemePreference(): Flow<ThemePreference> =
        context.dataStore.data.map { prefs ->
            when (prefs[KEY_THEME]) {
                "LIGHT" -> ThemePreference.LIGHT
                "DARK" -> ThemePreference.DARK
                else -> ThemePreference.SYSTEM
            }
        }

    override suspend fun setThemePreference(theme: ThemePreference) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = theme.name
        }
    }
}