package xyz.superbet.supercoctails.domain.usecase

import xyz.superbet.supercoctails.data.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class SearchCocktailsUseCase(private val repository: CocktailRepository) {
    suspend operator fun invoke(query: String): List<Cocktail> {
        return repository.searchCocktails(query)
    }
}