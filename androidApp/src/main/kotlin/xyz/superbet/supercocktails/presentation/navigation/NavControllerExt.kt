package xyz.superbet.supercocktails.presentation.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

fun NavController.popBackStackOnce() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}
