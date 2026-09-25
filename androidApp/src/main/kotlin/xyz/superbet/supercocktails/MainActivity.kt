package xyz.superbet.supercocktails

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.android.ext.android.inject
import xyz.superbet.supercocktails.domain.model.ThemePreference
import xyz.superbet.supercocktails.domain.usecase.theme.GetThemePreferenceUseCase
import xyz.superbet.supercocktails.presentation.navigation.AppNavigation
import xyz.superbet.supercocktails.ui.theme.SuperCocktailsTheme

class MainActivity : ComponentActivity() {
    private val getThemePreference: GetThemePreferenceUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themePreference by getThemePreference().collectAsState(
                initial = ThemePreference.SYSTEM
            )
            SuperCocktailsTheme(themePreference = themePreference) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}