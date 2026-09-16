package xyz.superbet.supercoctails.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import xyz.superbet.supercoctails.domain.model.Ingredient

class Converters {
    @TypeConverter
    fun fromIngredients(ingredients: List<Ingredient>): String = Json.encodeToString(ingredients)

    @TypeConverter
    fun toIngredients(json: String): List<Ingredient> = Json.decodeFromString(json)
}
