package xyz.superbet.supercoctails.domain.usecase

import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class GetRecommendedCocktailsUseCase(private val repository: CocktailRepository) {
    suspend operator fun invoke(): List<Cocktail> = repository.getRecommendedCocktails()
}