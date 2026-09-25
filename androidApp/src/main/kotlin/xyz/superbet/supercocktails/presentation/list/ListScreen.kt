package xyz.superbet.supercocktails.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import xyz.superbet.supercocktails.domain.model.ThemePreference
import xyz.superbet.supercocktails.presentation.list.element.CocktailRow
import xyz.superbet.supercocktails.presentation.list.element.ListTopBar
import xyz.superbet.supercocktails.presentation.list.element.RecentSearchesPanel
import xyz.superbet.supercocktails.presentation.state.ListUiState
import xyz.superbet.supercocktails.ui.component.EmptyState
import xyz.superbet.supercocktails.ui.component.ErrorState
import xyz.superbet.supercocktails.ui.component.LoadingState

@Composable
fun ListScreen(onCocktailClick: (String) -> Unit = {}) {
    val viewModel: ListViewModel = koinViewModel()
    val uiState: ListUiState by viewModel.uiState.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val isSearchFocused by viewModel.isSearchFocused.collectAsState()
    val themePreference by viewModel.themePreference.collectAsState()

    ListScreenContent(
        uiState = uiState,
        query = query,
        isSearchFocused = isSearchFocused,
        themePreference = themePreference,
        onQueryChanged = { viewModel.onQueryChanged(it) },
        onSearchFocused = { viewModel.onSearchFocused() },
        onSearchUnfocused = { viewModel.onSearchUnfocused() },
        onSearchSubmitted = { viewModel.onSearchSubmitted() },
        onThemeSelected = { viewModel.setTheme(it) },
        onCocktailClick = { id ->
            viewModel.onCocktailClicked()
            onCocktailClick(id)
        },
        onFavoriteClick = { viewModel.toggleFavorite(it) },
        onRecentSearchClick = { term ->
            viewModel.onQueryChanged(term)
            viewModel.onSearchSubmitted()
        },
        onRecentSearchDelete = { viewModel.deleteRecentSearch(it) },
        onRetry = { viewModel.retry() },
    )
}

@Composable
@Preview
fun ListScreenContent(
    uiState: ListUiState = ListUiState.Loading,
    query: String = "",
    isSearchFocused: Boolean = false,
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    onQueryChanged: (String) -> Unit = {},
    onSearchFocused: () -> Unit = {},
    onSearchUnfocused: () -> Unit = {},
    onSearchSubmitted: () -> Unit = {},
    onThemeSelected: (ThemePreference) -> Unit = {},
    onCocktailClick: (String) -> Unit = {},
    onFavoriteClick: (String) -> Unit = {},
    onRecentSearchClick: (String) -> Unit = {},
    onRecentSearchDelete: (String) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        ListTopBar(
            query = query,
            onQueryChanged = onQueryChanged,
            onSearchFocused = onSearchFocused,
            onSearchUnfocused = onSearchUnfocused,
            onSearchSubmitted = onSearchSubmitted,
            showRecommendedLabel = uiState is ListUiState.Content && query.isBlank(),
            isSearchFocused = isSearchFocused,
            currentTheme = themePreference,
            onThemeSelected = onThemeSelected,
        )

        when (uiState) {
            is ListUiState.Loading -> LoadingState(message = "Loading cocktails…")

            is ListUiState.SearchFocused -> RecentSearchesPanel(
                recentSearches = uiState.recentSearches,
                onTermClick = onRecentSearchClick,
                onTermDelete = onRecentSearchDelete,
            )

            is ListUiState.Content -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.cocktails, key = { it.id }) { cocktail ->
                    CocktailRow(
                        cocktail = cocktail,
                        onClick = { onCocktailClick(cocktail.id) },
                        onFavoriteClick = { onFavoriteClick(cocktail.id) },
                    )
                }
            }

            is ListUiState.Empty -> EmptyState(
                heading = "No Results",
                message = if (query.isNotBlank())
                    "No cocktails found for \"$query\". Try another name."
                else
                    "No cocktails found.",
            )

            is ListUiState.Error -> ErrorState(
                message = uiState.message,
                onRetry = onRetry,
            )
        }
    }
}