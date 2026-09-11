package xyz.superbet.supercoctails.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cocktails")
data class CocktailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val alcoholic: String?,
    val thumbnail: String?,
    val isFavorite: Boolean = false
)