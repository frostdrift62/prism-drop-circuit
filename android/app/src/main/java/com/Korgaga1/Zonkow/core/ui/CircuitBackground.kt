package com.Korgaga1.Zonkow.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.Korgaga1.Zonkow.R
import com.Korgaga1.Zonkow.core.theme.AccentCyan

@Composable
fun CircuitBackground(
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    bloom: Color = Color.Transparent,
    bloomCenter: Offset = Offset(0.82f, 0.12f),
    showTrace: Boolean = true
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        if (showTrace) {
            CircuitTrace(modifier = Modifier.fillMaxSize())
        }
        if (bloom != Color.Transparent) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width * bloomCenter.x, size.height * bloomCenter.y)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(bloom, Color.Transparent),
                        center = center,
                        radius = size.minDimension * 0.75f
                    ),
                    radius = size.minDimension * 0.75f,
                    center = center
                )
            }
        }
    }
}

@Composable
private fun CircuitTrace(modifier: Modifier = Modifier) {
    val line = Color.White.copy(alpha = 0.05f)
    val node = AccentCyan.copy(alpha = 0.16f)
    Canvas(modifier = modifier) {
        val stepX = size.width / 6f
        val stepY = size.height / 11f
        for (column in 1 until 6) {
            val x = stepX * column
            drawLine(
                color = line,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
        }
        for (row in 1 until 11) {
            val y = stepY * row
            drawLine(
                color = line,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            for (column in 1 until 6) {
                if ((row + column) % 3 == 0) {
                    drawCircle(color = node, radius = 2.5f, center = Offset(stepX * column, y))
                }
            }
        }
    }
}
