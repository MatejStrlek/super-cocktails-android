package xyz.superbet.supercocktails.domain.usecase.cocktail

import xyz.superbet.supercocktails.domain.model.Cocktail
import xyz.superbet.supercocktails.domain.repository.CocktailRepository

class GetCocktailByIdUseCase(private val repository: CocktailRepository) {
    suspend operator fun invoke(id: String): Cocktail? = repository.getCocktailById(id)
}
