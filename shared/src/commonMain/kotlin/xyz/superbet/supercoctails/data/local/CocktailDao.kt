package xyz.superbet.supercoctails.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {
    @Query("SELECT * FROM cocktails WHERE name LIKE '%' || :query || '%'")
    fun searchCocktails(query: String): Flow<List<CocktailEntity>>

    @Upsert
    suspend fun upsertCocktails(cocktails: List<CocktailEntity>)

    @Query("SELECT * FROM cocktails WHERE isRecommended = 1")
    fun getRecommendedCocktails(): Flow<List<CocktailEntity>>

    @Query("SELECT * FROM cocktails WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<CocktailEntity>

    @Query("SELECT * FROM cocktails WHERE id = :id")
    suspend fun getById(id: String): CocktailEntity?

    @Query("UPDATE cocktails SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: String)
}