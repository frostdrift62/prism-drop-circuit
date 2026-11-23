package com.Korgaga1.Zonkow.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Korgaga1.Zonkow.core.theme.TextPrimary

@Composable
fun PrismTile(
    accent: Color,
    mark: String,
    label: String,
    state: String,
    flashing: Boolean,
    highContrast: Boolean,
    enabled: Boolean,
    reducedMotion: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetScale = if (flashing && !reducedMotion) 1.12f else 1f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f)
    )
    val glow by animateFloatAsState(
        targetValue = if (flashing) 1f else 0.42f,
        animationSpec = tween(durationMillis = 180)
    )
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .size(64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(glow)
            .background(
                brush = Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.95f), accent.copy(alpha = 0.45f))
                ),
                shape = shape
            )
            .border(
                border = BorderStroke(
                    width = if (highContrast) 2.dp else 1.dp,
                    color = if (highContrast) TextPrimary else accent.copy(alpha = 0.8f)
                ),
                shape = shape
            )
            .clickable(enabled = enabled, onClick = onTap)
            .semantics {
                contentDescription = label
                stateDescription = state
            },
        contentAlignment = Alignment.Center
    ) {
        if (highContrast || flashing) {
            Text(
                text = mark,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = if (highContrast) 28.sp else 22.sp,
                color = TextPrimary
            )
        }
    }
}
