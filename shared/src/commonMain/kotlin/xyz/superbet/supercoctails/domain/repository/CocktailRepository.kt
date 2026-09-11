package xyz.superbet.supercoctails.domain.repository

import xyz.superbet.supercoctails.data.model.Cocktail

interface CocktailRepository {
    suspend fun searchCocktails(query: String): List<Cocktail>
}