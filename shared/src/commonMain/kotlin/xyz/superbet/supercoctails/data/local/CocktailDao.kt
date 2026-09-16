package xyz.superbet.supercoctails.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CocktailDao {
    @Query("SELECT * FROM cocktails WHERE name LIKE '%' || :query || '%'")
    suspend fun searchCocktails(query: String): List<CocktailEntity>

    @Upsert
    suspend fun upsertCocktails(cocktails: List<CocktailEntity>)

    @Query("SELECT * FROM cocktails WHERE isRecommended = 1")
    suspend fun getRecommendedCocktails(): List<CocktailEntity>

    @Query("SELECT * FROM cocktails WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<CocktailEntity>
}