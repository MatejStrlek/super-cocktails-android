package xyz.superbet.supercocktails.data.mapper

import xyz.superbet.supercocktails.data.local.CocktailEntity
import xyz.superbet.supercocktails.domain.model.Cocktail
import xyz.superbet.supercocktails.data.model.CocktailDto
import xyz.superbet.supercocktails.domain.model.Ingredient

fun CocktailDto.toCocktail() = Cocktail(
    id = id,
    name = name,
    category = category,
    alcoholic = alcoholic,
    thumbnail = thumbnail,
    glass = glass,
    instructions = instructions,
    dateModified = dateModified,
    ingredients = listOfNotNull(
        ingredient1?.let { Ingredient(it, measure1) },
        ingredient2?.let { Ingredient(it, measure2) },
        ingredient3?.let { Ingredient(it, measure3) },
        ingredient4?.let { Ingredient(it, measure4) },
        ingredient5?.let { Ingredient(it, measure5) },
        ingredient6?.let { Ingredient(it, measure6) },
        ingredient7?.let { Ingredient(it, measure7) },
        ingredient8?.let { Ingredient(it, measure8) },
        ingredient9?.let { Ingredient(it, measure9) },
        ingredient10?.let { Ingredient(it, measure10) },
        ingredient11?.let { Ingredient(it, measure11) },
        ingredient12?.let { Ingredient(it, measure12) },
        ingredient13?.let { Ingredient(it, measure13) },
        ingredient14?.let { Ingredient(it, measure14) },
        ingredient15?.let { Ingredient(it, measure15) },
    )
)

fun CocktailEntity.toDomainModel() = Cocktail(
    id = id,
    name = name,
    category = category,
    alcoholic = alcoholic,
    thumbnail = thumbnail,
    glass = glass,
    instructions = instructions,
    dateModified = dateModified,
    ingredients = ingredients,
    isFavorite = isFavorite,
)

fun Cocktail.toEntityModel() = CocktailEntity(
    id = id,
    name = name,
    category = category,
    alcoholic = alcoholic,
    thumbnail = thumbnail,
    glass = glass,
    instructions = instructions,
    dateModified = dateModified,
    ingredients = ingredients,
)
