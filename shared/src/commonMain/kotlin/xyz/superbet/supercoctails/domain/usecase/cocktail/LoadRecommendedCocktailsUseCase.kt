package xyz.superbet.supercoctails.domain.usecase.cocktail

import xyz.superbet.supercoctails.domain.algorithm.assembleRecommendedCocktails
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

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