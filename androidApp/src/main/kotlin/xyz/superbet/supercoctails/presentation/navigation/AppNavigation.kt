package xyz.superbet.supercoctails.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xyz.superbet.supercoctails.ListScreen
import xyz.superbet.supercoctails.presentation.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("list") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("list") {
            ListScreen()
        }
    }
}