package xyz.superbet.supercoctails.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RetryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.error,
        modifier = modifier,
    ) {
        Text(
            text = "Retry",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            color = MaterialTheme.colorScheme.onError,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
private fun RetryButtonPreview() {
    RetryButton(onClick = {})
}