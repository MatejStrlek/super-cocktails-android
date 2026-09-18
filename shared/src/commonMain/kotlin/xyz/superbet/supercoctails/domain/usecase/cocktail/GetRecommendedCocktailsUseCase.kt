package xyz.superbet.supercoctails.domain.usecase.cocktail

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class GetRecommendedCocktailsUseCase(private val repository: CocktailRepository) {
    operator fun invoke(): Flow<List<Cocktail>> = repository.getRecommendedCocktails()
}
