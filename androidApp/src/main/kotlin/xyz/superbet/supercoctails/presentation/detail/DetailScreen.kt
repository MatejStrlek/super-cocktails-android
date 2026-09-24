@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package xyz.superbet.supercoctails.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.model.Ingredient
import xyz.superbet.supercoctails.presentation.state.DetailUiState
import xyz.superbet.supercoctails.ui.component.ErrorState
import xyz.superbet.supercoctails.ui.component.LoadingState
import xyz.superbet.supercoctails.ui.component.TagChip

@Composable
fun DetailScreen(cocktailId: String, onBack: () -> Unit) {
    val viewModel: DetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(cocktailId) { viewModel.load(cocktailId) }

    DetailScreenContent(
        uiState = uiState,
        onBack = onBack,
        onFavoriteClick = { viewModel.toggleFavorite(it) },
    )
}

@Composable
@Preview
fun DetailScreenContent(
    uiState: DetailUiState = DetailUiState.Loading,
    onBack: () -> Unit = {},
    onFavoriteClick: (String) -> Unit = {},
) {
    when (uiState) {
        is DetailUiState.Loading -> LoadingState()

        is DetailUiState.Error -> ErrorState(message = "Could not load cocktail.")

        is DetailUiState.Content -> DetailContent(
            cocktail = uiState.cocktail,
            onBack = onBack,
            onFavoriteClick = { onFavoriteClick(uiState.cocktail.id) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(cocktail: Cocktail, onBack: () -> Unit, onFavoriteClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero image with floating back button on top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            AsyncImage(
                model = cocktail.thumbnail,
                contentDescription = cocktail.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(50)

                        )
                        .padding(8.dp),
                    tint = Color.White
                )
            }
        }

        // Body content
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tags row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                cocktail.category?.let { TagChip(it) }
                cocktail.glass?.let { TagChip(it) }
                cocktail.alcoholic?.let { TagChip(it, highlighted = true) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cocktail.name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (cocktail.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (cocktail.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (cocktail.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cocktail.ingredients.isNotEmpty()) {
                Text(
                    text = "INGREDIENTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column {
                        cocktail.ingredients.forEachIndexed { index, ingredient ->
                            IngredientRow(ingredient)
                            if (index < cocktail.ingredients.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            cocktail.instructions?.let { instructions ->
                Text(
                    text = "INSTRUCTIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = instructions, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }

            cocktail.dateModified?.let { date ->
                Text(
                    text = "Updated $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        ingredient.measure?.takeIf { it.isNotBlank() }?.let { measure ->
            Text(
                text = measure,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}