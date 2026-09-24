package xyz.superbet.supercoctails.presentation.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.superbet.supercoctails.domain.model.ThemePreference
import xyz.superbet.supercoctails.domain.repository.ThemeRepository

class FakeThemeRepository : ThemeRepository {
    private val theme = MutableStateFlow(ThemePreference.SYSTEM)

    override fun getThemePreference(): Flow<ThemePreference> = theme

    override suspend fun setThemePreference(theme: ThemePreference) {
        this.theme.value = theme
    }
}