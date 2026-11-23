package com.Korgaga1.Zonkow.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Korgaga1.Zonkow.R
import com.Korgaga1.Zonkow.core.di.ServiceLocator
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.AccentViolet
import com.Korgaga1.Zonkow.core.theme.SurfaceElevated
import com.Korgaga1.Zonkow.core.theme.TextPrimary
import com.Korgaga1.Zonkow.core.theme.TextSecondary
import com.Korgaga1.Zonkow.core.ui.CircuitBackground
import com.Korgaga1.Zonkow.core.ui.cornerBrackets

private val LoaderOverlay = listOf(
    Color(0xF20A0014),
    Color(0xE61A0038),
    Color(0xF20A0014)
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.finished) {
        if (state.finished) {
            onFinished()
        }
    }

    SplashContent(state = state)
}

@Composable
private fun SplashContent(state: SplashUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
        CircuitBackground(overlay = LoaderOverlay, showTrace = !state.reducedMotion)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandEmblem(reducedMotion = state.reducedMotion)
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 36.dp)
            )
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelLarge,
                color = AccentCyan.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            LinearProgressIndicator(
                color = AccentViolet,
                trackColor = SurfaceElevated,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .padding(top = 44.dp)
                    .width(180.dp)
                    .height(4.dp)
            )
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 14.dp)
            )
        }
    }
}

@Composable
private fun BrandEmblem(reducedMotion: Boolean) {
    val loops = rememberInfiniteTransition()
    val spin by loops.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing)
        )
    )
    val pulse by loops.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val spark by loops.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing)
        )
    )
    val rotation = if (reducedMotion) 0f else spin
    val glow = if (reducedMotion) 0.55f else pulse

    Box(
        modifier = Modifier
            .size(216.dp)
            .background(
                color = SurfaceElevated.copy(alpha = 0.42f),
                shape = RoundedCornerShape(28.dp)
            )
            .cornerBrackets(AccentCyan.copy(alpha = 0.6f), arm = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val radius = size.minDimension / 2f
            drawCircle(
                color = AccentViolet.copy(alpha = 0.30f),
                radius = radius - 4f,
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = AccentCyan.copy(alpha = 0.20f),
                radius = radius - 22f,
                style = Stroke(width = 2f)
            )
        }
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer { rotationZ = rotation }
        ) {
            val radius = size.minDimension / 2f - 4f
            val center = Offset(size.width / 2f, size.height / 2f)
            for (tick in 0 until 8) {
                val angle = tick * 45.0
                val radians = Math.toRadians(angle)
                val outer = Offset(
                    x = center.x + (radius * kotlin.math.cos(radians)).toFloat(),
                    y = center.y + (radius * kotlin.math.sin(radians)).toFloat()
                )
                val inner = Offset(
                    x = center.x + ((radius - 16f) * kotlin.math.cos(radians)).toFloat(),
                    y = center.y + ((radius - 16f) * kotlin.math.sin(radians)).toFloat()
                )
                drawLine(
                    color = AccentCyan.copy(alpha = 0.55f),
                    start = inner,
                    end = outer,
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }
        if (!reducedMotion) {
            Canvas(modifier = Modifier.size(200.dp)) {
                for (index in 0 until 3) {
                    val offset = (spark + index * 0.33f) % 1f
                    val y = size.height * offset
                    drawLine(
                        color = AccentCyan.copy(alpha = 0.35f),
                        start = Offset(size.width * 0.12f, y),
                        end = Offset(size.width * 0.30f, y),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = AccentViolet.copy(alpha = 0.35f),
                        start = Offset(size.width * 0.70f, size.height - y),
                        end = Offset(size.width * 0.88f, size.height - y),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.cd_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(136.dp)
                .alpha(0.35f + glow * 0.65f)
        )
    }
}
