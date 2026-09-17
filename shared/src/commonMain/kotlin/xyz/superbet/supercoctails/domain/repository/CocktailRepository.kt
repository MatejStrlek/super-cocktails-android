package xyz.superbet.supercoctails.domain.repository

import kotlinx.coroutines.flow.Flow
import xyz.superbet.supercoctails.domain.model.Cocktail

interface CocktailRepository {
    fun searchCocktails(query: String): Flow<List<Cocktail>>
    fun getRecommendedCocktails(): Flow<List<Cocktail>>
    suspend fun getCocktailById(id: String): Cocktail?
    suspend fun toggleFavorite(id: String)
}