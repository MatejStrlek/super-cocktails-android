package xyz.superbet.supercocktails.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object List : Route

    @Serializable
    data class Detail(val cocktailId: String) : Route
}