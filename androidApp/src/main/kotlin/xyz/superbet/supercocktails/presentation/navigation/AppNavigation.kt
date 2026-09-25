package xyz.superbet.supercocktails.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import xyz.superbet.supercocktails.presentation.detail.DetailScreen
import xyz.superbet.supercocktails.presentation.list.ListScreen
import xyz.superbet.supercocktails.presentation.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Splash) {
        composable<Route.Splash> {
            SplashScreen(onTimeout = {
                navController.navigate(Route.List) {
                    popUpTo(Route.Splash) { inclusive = true }
                }
            })
        }
        composable<Route.List> {
            ListScreen(onCocktailClick = { id -> navController.navigate(Route.Detail(id)) })
        }
        composable<Route.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Detail>()
            DetailScreen(
                cocktailId = route.cocktailId,
                onBack = { navController.popBackStackOnce() }
            )
        }
    }
}