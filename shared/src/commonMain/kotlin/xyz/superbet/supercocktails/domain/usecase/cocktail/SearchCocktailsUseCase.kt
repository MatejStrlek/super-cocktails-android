package xyz.superbet.supercocktails.domain.usecase.cocktail

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercocktails.domain.model.Cocktail
import xyz.superbet.supercocktails.domain.repository.CocktailRepository

class SearchCocktailsUseCase(private val repository: CocktailRepository) {
    operator fun invoke(query: String): Flow<List<Cocktail>> = repository.searchCocktails(query)
}
