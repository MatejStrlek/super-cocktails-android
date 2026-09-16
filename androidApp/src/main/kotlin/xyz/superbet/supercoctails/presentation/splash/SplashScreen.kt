package xyz.superbet.supercoctails.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import xyz.superbet.supercoctails.domain.usecase.GetRecommendedCocktailsUseCase
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val useCase: GetRecommendedCocktailsUseCase = koinInject()
    val appScope: CoroutineScope = koinInject()

    LaunchedEffect(Unit) {
        appScope.launch { runCatching { useCase() } }
        delay(1500.milliseconds)
        onTimeout()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Super Cocktails", style = MaterialTheme.typography.headlineLarge)
    }
}