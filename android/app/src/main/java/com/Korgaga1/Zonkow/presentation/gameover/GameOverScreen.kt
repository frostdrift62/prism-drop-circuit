package com.Korgaga1.Zonkow.presentation.gameover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Korgaga1.Zonkow.R
import com.Korgaga1.Zonkow.core.di.ServiceLocator
import com.Korgaga1.Zonkow.core.theme.AccentAmber
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.AccentMagenta
import com.Korgaga1.Zonkow.core.theme.AccentViolet
import com.Korgaga1.Zonkow.core.theme.BaseVoid
import com.Korgaga1.Zonkow.core.theme.TextPrimary
import com.Korgaga1.Zonkow.core.theme.TextSecondary
import com.Korgaga1.Zonkow.core.ui.CircuitBackground
import com.Korgaga1.Zonkow.core.ui.HudCard
import com.Korgaga1.Zonkow.core.ui.PrimaryButton
import com.Korgaga1.Zonkow.core.ui.SecondaryButton
import com.Korgaga1.Zonkow.core.ui.StatCard
import com.Korgaga1.Zonkow.core.ui.formatCharge
import com.Korgaga1.Zonkow.domain.model.RunResult

private val SuccessOverlay = listOf(
    Color(0xF20A0014),
    Color(0xCC10083A),
    Color(0xF20A0014)
)

private val BurnoutOverlay = listOf(
    Color(0xF20A0014),
    Color(0xCC2A0A1E),
    Color(0xF20A0014)
)

@Composable
fun GameOverScreen(
    result: RunResult,
    onReplay: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(factory = ServiceLocator.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        viewModel.refresh()
    }

    GameOverContent(
        result = result,
        state = state,
        onReplay = onReplay,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    result: RunResult,
    state: GameOverUiState,
    onReplay: () -> Unit,
    onMenu: () -> Unit
) {
    val accent = if (result.burnedOut) AccentMagenta else AccentCyan
    val flare = remember { Animatable(1f) }
    LaunchedEffect(result) {
        flare.animateTo(
            targetValue = 1.18f,
            animationSpec = repeatable(
                iterations = 4,
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        flare.snapTo(1f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CircuitBackground(
            overlay = if (result.burnedOut) BurnoutOverlay else SuccessOverlay,
            bloom = accent.copy(alpha = 0.18f),
            bloomCenter = androidx.compose.ui.geometry.Offset(0.5f, 0.22f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = stringResource(R.string.cd_beacon),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(84.dp)
                        .graphicsLayer {
                            scaleX = flare.value
                            scaleY = flare.value
                        }
                )
            }
            Text(
                text = stringResource(
                    if (result.burnedOut) {
                        R.string.circuit_burned_out
                    } else {
                        R.string.circuit_complete
                    }
                ),
                style = MaterialTheme.typography.displaySmall,
                fontSize = 30.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 18.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
            HudCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                corner = 22.dp,
                borderStart = accent.copy(alpha = 0.55f),
                borderEnd = AccentViolet.copy(alpha = 0.4f),
                bracketColor = accent.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (result.newBest) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = AccentAmber,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.new_best),
                                style = MaterialTheme.typography.labelSmall,
                                color = BaseVoid
                            )
                        }
                    }
                    Text(
                        text = formatCharge(result.score),
                        style = MaterialTheme.typography.displaySmall,
                        fontSize = 46.sp,
                        color = accent,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text(
                        text = stringResource(R.string.score_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(94.dp)
                            .padding(top = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            label = stringResource(R.string.stat_drops),
                            value = result.drops.toString(),
                            accent = AccentViolet,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        StatCard(
                            label = stringResource(R.string.stat_streak),
                            value = result.bestStreak.toString(),
                            accent = AccentAmber,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        StatCard(
                            label = stringResource(R.string.stat_targets),
                            value = result.targetsHit.toString(),
                            accent = AccentCyan,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.result_hint, formatCharge(state.bestScore)),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            PrimaryButton(
                label = stringResource(R.string.drop_again),
                onClick = onReplay,
                height = 60.dp,
                modifier = Modifier.fillMaxWidth()
            )
            SecondaryButton(
                label = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp)
            )
        }
    }
}
