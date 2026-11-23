package com.Korgaga1.Zonkow.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.InertLane

@Composable
fun IntegrityRow(
    charges: Int,
    maximum: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.semantics {
            stateDescription = description
            liveRegion = LiveRegionMode.Polite
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(maximum) { index ->
            val lit = index < charges
            val color by animateColorAsState(
                targetValue = if (lit) AccentCyan else InertLane,
                animationSpec = tween(durationMillis = 280)
            )
            Box(
                modifier = Modifier
                    .size(11.dp)
                    .graphicsLayer { rotationZ = 45f }
                    .background(color = color, shape = RoundedCornerShape(2.dp))
            )
        }
    }
}
