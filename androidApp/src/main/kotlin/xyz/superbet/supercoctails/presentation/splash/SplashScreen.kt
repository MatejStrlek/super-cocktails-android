package xyz.superbet.supercoctails.presentation.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    koinViewModel<SplashViewModel>()
    SplashScreenContent(onTimeout = onTimeout)
}

@Composable
@Preview
fun SplashScreenContent(onTimeout: () -> Unit = {}) {
    LaunchedEffect(Unit) {
        delay(1500.milliseconds)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MartiniGlassIcon(modifier = Modifier.size(44.dp, 54.dp))

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "SUPER",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "COCKTAILS",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Tonight's pour, sorted.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun MartiniGlassIcon(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val strokeWidth = 1.8.dp.toPx()
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height

        val bowlApexY = h * 0.556f
        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w / 2f, bowlApexY)
                close()
            },
            color = color,
            style = stroke
        )

        drawLine(
            color = color,
            start = Offset(w / 2f, bowlApexY),
            end = Offset(w / 2f, h * 0.963f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(w * 0.205f, h * 0.963f),
            end = Offset(w * 0.795f, h * 0.963f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}