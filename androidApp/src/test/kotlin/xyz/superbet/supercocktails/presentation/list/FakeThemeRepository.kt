package xyz.superbet.supercocktails.presentation.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.superbet.supercocktails.domain.model.ThemePreference
import xyz.superbet.supercocktails.domain.repository.ThemeRepository

class FakeThemeRepository : ThemeRepository {
    private val theme = MutableStateFlow(ThemePreference.SYSTEM)

    override fun getThemePreference(): Flow<ThemePreference> = theme

    override suspend fun setThemePreference(theme: ThemePreference) {
        this.theme.value = theme
    }
}