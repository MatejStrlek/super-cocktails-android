package xyz.superbet.supercoctails.presentation.splash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.domain.usecase.cocktail.LoadRecommendedCocktailsUseCase

class SplashViewModel(
    private val loadRecommendedCocktailsUseCase: LoadRecommendedCocktailsUseCase,
    appScope: CoroutineScope,
) : ViewModel() {
    init {
        appScope.launch {
            runCatching { loadRecommendedCocktailsUseCase() }
                .onFailure { if (it is CancellationException) throw it }
        }
    }
}