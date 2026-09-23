package xyz.superbet.supercoctails.domain.usecase.theme

import xyz.superbet.supercoctails.domain.model.ThemePreference
import xyz.superbet.supercoctails.domain.repository.ThemeRepository

class SetThemePreferenceUseCase(private val repository: ThemeRepository) {
    suspend operator fun invoke(theme: ThemePreference) = repository.setThemePreference(theme)
}