package xyz.superbet.supercoctails.data.mapper

import xyz.superbet.supercoctails.data.local.CocktailEntity
import xyz.superbet.supercoctails.data.model.Cocktail

fun CocktailEntity.toDomainModel() = Cocktail(
    id = id,
    name = name,
    category = category,
    alcoholic = alcoholic,
    thumbnail = thumbnail
)

fun Cocktail.toEntityModel() = CocktailEntity(
    id = id,
    name = name,
    category = category,
    alcoholic = alcoholic,
    thumbnail = thumbnail
)
