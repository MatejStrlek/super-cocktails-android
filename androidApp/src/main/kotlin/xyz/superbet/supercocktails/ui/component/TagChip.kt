package xyz.superbet.supercocktails.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TagChip(
    label: String,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val background = if (highlighted) MaterialTheme.colorScheme.errorContainer
                     else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (highlighted) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = background,
        modifier = modifier,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Preview
@Composable
private fun TagChipPreview() {
    TagChip(label = "Alcoholic", highlighted = true)
}