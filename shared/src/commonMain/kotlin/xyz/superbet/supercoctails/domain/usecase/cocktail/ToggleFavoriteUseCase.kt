package xyz.superbet.supercoctails.domain.usecase.cocktail

import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class ToggleFavoriteUseCase(private val repository: CocktailRepository) {
    suspend operator fun invoke(id: String) = repository.toggleFavorite(id)
}
