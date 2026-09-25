package xyz.superbet.supercocktails.domain.usecase.theme

import xyz.superbet.supercocktails.domain.model.ThemePreference
import xyz.superbet.supercocktails.domain.repository.ThemeRepository

class SetThemePreferenceUseCase(private val repository: ThemeRepository) {
    suspend operator fun invoke(theme: ThemePreference) = repository.setThemePreference(theme)
}