package xyz.superbet.supercoctails.domain.usecase

import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class GetCocktailByIdUseCase(private val repository: CocktailRepository) {
    suspend operator fun invoke(id: String): Cocktail? = repository.getCocktailById(id)
}
