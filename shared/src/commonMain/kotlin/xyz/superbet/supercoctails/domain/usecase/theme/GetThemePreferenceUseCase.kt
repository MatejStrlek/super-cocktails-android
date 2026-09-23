package xyz.superbet.supercoctails.domain.usecase.theme

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercoctails.domain.model.ThemePreference
import xyz.superbet.supercoctails.domain.repository.ThemeRepository

class GetThemePreferenceUseCase(private val repository: ThemeRepository) {
    operator fun invoke(): Flow<ThemePreference> = repository.getThemePreference()
}