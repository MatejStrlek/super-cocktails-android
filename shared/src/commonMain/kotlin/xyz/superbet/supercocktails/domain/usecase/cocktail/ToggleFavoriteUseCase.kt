package xyz.superbet.supercocktails.domain.usecase.cocktail

import xyz.superbet.supercocktails.domain.repository.CocktailRepository

class ToggleFavoriteUseCase(private val repository: CocktailRepository) {
    suspend operator fun invoke(id: String) = repository.toggleFavorite(id)
}
