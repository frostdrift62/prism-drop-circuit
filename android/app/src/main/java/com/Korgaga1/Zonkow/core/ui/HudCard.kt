package com.Korgaga1.Zonkow.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.AccentViolet
import com.Korgaga1.Zonkow.core.theme.SurfaceCircuit

@Composable
fun HudCard(
    modifier: Modifier = Modifier,
    corner: Dp = 20.dp,
    container: Color = SurfaceCircuit.copy(alpha = 0.82f),
    borderStart: Color = AccentViolet.copy(alpha = 0.55f),
    borderEnd: Color = AccentCyan.copy(alpha = 0.45f),
    borderWidth: Dp = 1.2.dp,
    bracketColor: Color = AccentCyan.copy(alpha = 0.7f),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .background(color = container, shape = shape)
            .border(
                border = BorderStroke(
                    width = borderWidth,
                    brush = Brush.linearGradient(listOf(borderStart, borderEnd))
                ),
                shape = shape
            )
            .cornerBrackets(bracketColor),
        content = content
    )
}

fun Modifier.cornerBrackets(color: Color, arm: Dp = 16.dp, width: Dp = 1.5.dp): Modifier =
    this.drawBehind {
        val armPx = arm.toPx()
        val strokePx = width.toPx()
        val inset = strokePx * 2f
        val right = size.width - inset
        val bottom = size.height - inset
        drawLine(color, Offset(inset, inset + armPx), Offset(inset, inset), strokePx, StrokeCap.Round)
        drawLine(color, Offset(inset, inset), Offset(inset + armPx, inset), strokePx, StrokeCap.Round)
        drawLine(color, Offset(right - armPx, inset), Offset(right, inset), strokePx, StrokeCap.Round)
        drawLine(color, Offset(right, inset), Offset(right, inset + armPx), strokePx, StrokeCap.Round)
        drawLine(color, Offset(inset, bottom - armPx), Offset(inset, bottom), strokePx, StrokeCap.Round)
        drawLine(color, Offset(inset, bottom), Offset(inset + armPx, bottom), strokePx, StrokeCap.Round)
        drawLine(color, Offset(right - armPx, bottom), Offset(right, bottom), strokePx, StrokeCap.Round)
        drawLine(color, Offset(right, bottom), Offset(right, bottom - armPx), strokePx, StrokeCap.Round)
    }
