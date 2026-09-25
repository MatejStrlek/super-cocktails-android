package xyz.superbet.supercocktails.domain.usecase.theme

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercocktails.domain.model.ThemePreference
import xyz.superbet.supercocktails.domain.repository.ThemeRepository

class GetThemePreferenceUseCase(private val repository: ThemeRepository) {
    operator fun invoke(): Flow<ThemePreference> = repository.getThemePreference()
}