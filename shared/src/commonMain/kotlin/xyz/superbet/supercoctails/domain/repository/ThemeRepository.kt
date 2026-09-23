package xyz.superbet.supercoctails.domain.repository

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercoctails.domain.model.ThemePreference

interface ThemeRepository {
    fun getThemePreference(): Flow<ThemePreference>
    suspend fun setThemePreference(theme: ThemePreference)
}