package com.Korgaga1.Zonkow.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Korgaga1.Zonkow.R
import kotlin.math.roundToInt

@Composable
fun GateSprite(
    index: Int,
    facingRight: Boolean,
    enabled: Boolean,
    description: String,
    state: String,
    hitSize: Dp,
    spriteSize: Dp,
    laneWidthPx: Float,
    onFlip: (Int) -> Unit,
    onSlide: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dragged = remember { mutableFloatStateOf(0f) }
    val facing by animateFloatAsState(
        targetValue = if (facingRight) 0f else 180f,
        animationSpec = tween(durationMillis = 220)
    )
    Box(
        modifier = modifier
            .size(hitSize)
            .alpha(if (enabled) 1f else 0.45f)
            .clickable(enabled = enabled) { onFlip(index) }
            .pointerInput(enabled, laneWidthPx) {
                if (!enabled) {
                    return@pointerInput
                }
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val lanes = if (laneWidthPx > 0f) {
                            (dragged.floatValue / laneWidthPx).roundToInt()
                        } else {
                            0
                        }
                        dragged.floatValue = 0f
                        if (lanes != 0) {
                            onSlide(index, lanes)
                        }
                    },
                    onDragCancel = { dragged.floatValue = 0f }
                ) { change, amount ->
                    change.consume()
                    dragged.floatValue += amount
                }
            }
            .semantics { stateDescription = state },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(spriteSize)
                .graphicsLayer { rotationY = facing }
        )
    }
}

val GateMinimumTouchTarget: Dp = 48.dp
