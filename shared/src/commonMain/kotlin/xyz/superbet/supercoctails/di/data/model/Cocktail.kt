package xyz.superbet.supercoctails.di.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@OptIn(InternalSerializationApi::class)
@Serializable
data class CocktailResponse(
    @SerialName("drinks") val drinks: List<Cocktail>?
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class Cocktail(
    @SerialName("idDrink") val id: String,
    @SerialName("strDrink") val name: String,
    @SerialName("strCategory") val category: String? = null,
    @SerialName("strAlcoholic") val alcoholic: String? = null,
    @SerialName("strDrinkThumb") val thumbnail: String? = null
)