package xyz.superbet.supercocktails.domain.repository

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercocktails.domain.model.Cocktail

interface CocktailRepository {
    fun searchCocktails(query: String): Flow<List<Cocktail>>
    suspend fun searchCocktailsDirect(query: String): List<Cocktail>
    fun observeRecommendedCocktails(): Flow<List<Cocktail>>
    suspend fun saveRecommendedCocktails(cocktails: List<Cocktail>)
    suspend fun hasRecommendedCocktails(): Boolean
    suspend fun getCocktailById(id: String): Cocktail?
    suspend fun toggleFavorite(id: String)
}