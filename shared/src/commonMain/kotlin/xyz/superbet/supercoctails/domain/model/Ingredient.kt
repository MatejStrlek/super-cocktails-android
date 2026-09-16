package xyz.superbet.supercoctails.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class Ingredient(
    val name: String,
    val measure: String?,
)