package xyz.superbet.supercoctails.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import xyz.superbet.supercoctails.domain.model.ThemePreference
import xyz.superbet.supercoctails.presentation.list.element.CocktailRow
import xyz.superbet.supercoctails.presentation.list.element.ListTopBar
import xyz.superbet.supercoctails.presentation.list.element.RecentSearchesPanel
import xyz.superbet.supercoctails.presentation.state.ListUiState

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
            is ListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading cocktails…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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

            is ListUiState.Empty -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (query.isNotBlank())
                            "No cocktails found for \"$query\". Try another name."
                        else
                            "No cocktails found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is ListUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Something Went Wrong",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Could not load cocktails. Check your connection and try again.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        onClick = onRetry,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = "Retry",
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}