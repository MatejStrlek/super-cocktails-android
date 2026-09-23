package xyz.superbet.supercoctails.domain.usecase.cocktail

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import xyz.superbet.supercoctails.domain.algorithm.assembleRecommendedCocktails
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class GetRecommendedCocktailsUseCase(private val repository: CocktailRepository) {
    operator fun invoke(): Flow<List<Cocktail>> =
        repository.observeRecommendedCocktails()
            .onStart {
                if (!repository.hasRecommendedCocktails()) {
                    val assembled = assembleRecommendedCocktails(
                        search = { query -> repository.searchCocktailsDirect(query) }
                    )
                    repository.saveRecommendedCocktails(assembled)
                }
            }
}