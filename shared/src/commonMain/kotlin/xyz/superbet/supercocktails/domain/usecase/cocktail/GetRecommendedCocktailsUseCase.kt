package xyz.superbet.supercocktails.domain.usecase.cocktail

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercocktails.domain.model.Cocktail
import xyz.superbet.supercocktails.domain.repository.CocktailRepository

class GetRecommendedCocktailsUseCase(private val repository: CocktailRepository) {
    operator fun invoke(): Flow<List<Cocktail>> = repository.observeRecommendedCocktails()
}