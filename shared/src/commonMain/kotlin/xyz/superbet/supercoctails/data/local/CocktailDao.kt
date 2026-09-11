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
}