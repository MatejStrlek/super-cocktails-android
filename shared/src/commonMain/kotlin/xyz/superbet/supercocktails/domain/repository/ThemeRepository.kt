package xyz.superbet.supercocktails.domain.repository

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercocktails.domain.model.ThemePreference

interface ThemeRepository {
    fun getThemePreference(): Flow<ThemePreference>
    suspend fun setThemePreference(theme: ThemePreference)
}