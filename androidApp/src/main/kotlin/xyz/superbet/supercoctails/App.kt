package xyz.superbet.supercoctails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import xyz.superbet.supercoctails.data.model.Cocktail
import xyz.superbet.supercoctails.domain.usecase.SearchCocktailsUseCase

@Composable
@Preview
fun App() {
    val useCase: SearchCocktailsUseCase = koinInject()
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }

    LaunchedEffect(Unit) {
        cocktails = useCase("Martini")
    }
    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            cocktails.forEach { cocktail ->
                Text(text = cocktail.name)
            }
        }
    }
}
