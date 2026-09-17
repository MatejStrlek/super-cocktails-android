package xyz.superbet.supercoctails.domain.repository

import xyz.superbet.supercoctails.domain.model.Cocktail

interface CocktailRepository {
    suspend fun searchCocktails(query: String): List<Cocktail>
    suspend fun getRecommendedCocktails(): List<Cocktail>
    suspend fun getCocktailById(id: String): Cocktail?
}