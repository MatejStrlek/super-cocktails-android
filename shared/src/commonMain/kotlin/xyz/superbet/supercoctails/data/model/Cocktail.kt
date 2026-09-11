@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package xyz.superbet.supercoctails.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CocktailResponse(
    @SerialName("drinks") val drinks: List<Cocktail>?
)

@Serializable
data class Cocktail(
    @SerialName("idDrink") val id: String,
    @SerialName("strDrink") val name: String,
    @SerialName("strCategory") val category: String? = null,
    @SerialName("strAlcoholic") val alcoholic: String? = null,
    @SerialName("strDrinkThumb") val thumbnail: String? = null
)