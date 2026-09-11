package xyz.superbet.supercoctails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import xyz.superbet.supercoctails.presentation.list.ListUiState
import xyz.superbet.supercoctails.presentation.list.ListViewModel

@Composable
@Preview
fun App() {
    val viewModel: ListViewModel = koinViewModel()
    val uiState: ListUiState by viewModel.uiState.collectAsState()

    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val state = uiState) {
                is ListUiState.Loading ->
                    Text(text = "Loading...")
                is ListUiState.Content -> {
                    state.cocktails.forEach { cocktail ->
                        Text(text = cocktail.name)
                    }
                }
            }
        }
    }
}
