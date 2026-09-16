package xyz.superbet.supercoctails.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.superbet.supercoctails.domain.model.Ingredient

@Entity(tableName = "cocktails")
data class CocktailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val alcoholic: String?,
    val thumbnail: String?,
    val glass: String? = null,
    val instructions: String? = null,
    val dateModified: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val isFavorite: Boolean = false,
    val isRecommended: Boolean = false,
)