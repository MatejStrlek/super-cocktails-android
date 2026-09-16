package xyz.superbet.supercoctails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import xyz.superbet.supercoctails.presentation.list.ListTopBar
import xyz.superbet.supercoctails.presentation.list.ListUiState
import xyz.superbet.supercoctails.presentation.list.ListViewModel

@Composable
@Preview
fun ListScreen() {
    val viewModel: ListViewModel = koinViewModel()
    val uiState: ListUiState by viewModel.uiState.collectAsState()

    var query by remember { mutableStateOf("") }
    var hasEverFocusedSearch by remember { mutableStateOf(false) }


    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
        ) {
            ListTopBar(
                query = query,
                onQueryChanged = {
                    query = it
                    viewModel.onQueryChanged(it)
                },
                hasEverFocusedSearch = hasEverFocusedSearch,
                showRecommendedLabel = uiState is ListUiState.Content && query.isBlank()
            )

            when (val state = uiState) {
                is ListUiState.Loading -> Text(text = "Loading...")
                is ListUiState.Content -> {
                    state.cocktails.forEach { cocktail ->
                        Text(text = cocktail.name)
                    }
                }
                is ListUiState.Empty -> Text(text = "No cocktails found.")
                is ListUiState.Error -> Text(text = "Error: ${state.message}")
            }
        }
    }
}
