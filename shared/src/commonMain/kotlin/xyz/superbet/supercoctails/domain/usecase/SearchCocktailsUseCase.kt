package xyz.superbet.supercoctails.domain.usecase

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class SearchCocktailsUseCase(private val repository: CocktailRepository) {
    operator fun invoke(query: String): Flow<List<Cocktail>> = repository.searchCocktails(query)
}