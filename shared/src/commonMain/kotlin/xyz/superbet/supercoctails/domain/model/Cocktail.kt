package xyz.superbet.supercoctails.domain.model

data class Cocktail(
    val id: String,
    val name: String,
    val category: String?,
    val alcoholic: String?,
    val thumbnail: String?,
    val glass: String? = null,
    val instructions: String? = null,
    val dateModified: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val isFavorite: Boolean = false
)