package xyz.superbet.supercoctails.presentation.list.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.superbet.supercoctails.domain.model.ThemePreference

@Composable
fun ListTopBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchFocused: () -> Unit,
    onSearchUnfocused: () -> Unit,
    onSearchSubmitted: () -> Unit,
    showRecommendedLabel: Boolean,
    isSearchFocused: Boolean,
    currentTheme: ThemePreference = ThemePreference.SYSTEM,
    onThemeSelected: (ThemePreference) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var themeMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Cocktails",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box {
                IconButton(onClick = { themeMenuExpanded = true }) {
                    Icon(
                        imageVector = when (currentTheme) {
                            ThemePreference.LIGHT -> Icons.Filled.LightMode
                            ThemePreference.DARK -> Icons.Filled.DarkMode
                            ThemePreference.SYSTEM -> Icons.Filled.BrightnessAuto
                        },
                        contentDescription = "Theme",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = themeMenuExpanded,
                    onDismissRequest = { themeMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("System default") },
                        leadingIcon = { Icon(Icons.Filled.BrightnessAuto, contentDescription = null) },
                        onClick = { onThemeSelected(ThemePreference.SYSTEM); themeMenuExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Light") },
                        leadingIcon = { Icon(Icons.Filled.LightMode, contentDescription = null) },
                        onClick = { onThemeSelected(ThemePreference.LIGHT); themeMenuExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Dark") },
                        leadingIcon = { Icon(Icons.Filled.DarkMode, contentDescription = null) },
                        onClick = { onThemeSelected(ThemePreference.DARK); themeMenuExpanded = false }
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = {
                Text(
                    text = "Search by name",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchSubmitted()
                focusManager.clearFocus()
            }),
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) onSearchFocused() else onSearchUnfocused()
                }
        )
        AnimatedVisibility(isSearchFocused) {
                TextButton(onClick = {
                    onQueryChanged("")
                    focusManager.clearFocus()
                }) {
                    Text("Cancel")
                }
            }
        }

        if (showRecommendedLabel) {
            Text(
                text = "RECOMMENDED FOR YOU",
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.4.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
