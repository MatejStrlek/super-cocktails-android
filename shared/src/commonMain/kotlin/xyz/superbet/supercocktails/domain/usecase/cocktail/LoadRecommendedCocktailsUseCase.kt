package xyz.superbet.supercocktails.domain.usecase.cocktail

import xyz.superbet.supercocktails.domain.algorithm.assembleRecommendedCocktails
import xyz.superbet.supercocktails.domain.repository.CocktailRepository

class LoadRecommendedCocktailsUseCase(private val repository: CocktailRepository) {
    suspend operator fun invoke() {
        if (!repository.hasRecommendedCocktails()) {
            val assembled = assembleRecommendedCocktails(
                search = { query -> repository.searchCocktailsDirect(query) }
            )
            repository.saveRecommendedCocktails(assembled)
        }
    }
}