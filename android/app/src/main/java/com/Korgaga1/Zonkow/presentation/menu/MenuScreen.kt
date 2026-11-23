package com.Korgaga1.Zonkow.presentation.menu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.Korgaga1.Zonkow.core.theme.AccentAmber
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.AccentMagenta
import com.Korgaga1.Zonkow.core.theme.AccentViolet
import com.Korgaga1.Zonkow.core.theme.SurfaceCircuit
import com.Korgaga1.Zonkow.core.theme.TextPrimary
import com.Korgaga1.Zonkow.core.theme.TextSecondary
import com.Korgaga1.Zonkow.core.ui.CircuitBackground
import com.Korgaga1.Zonkow.core.ui.HudCard
import com.Korgaga1.Zonkow.core.ui.SecondaryButton
import com.Korgaga1.Zonkow.core.ui.StatCard
import com.Korgaga1.Zonkow.core.ui.formatCharge
import com.Korgaga1.Zonkow.data.sample.SampleData

private val MenuOverlay = listOf(
    Color(0xB30A0014),
    Color(0x990D1B2A),
    Color(0xD91A0038)
)

private data class MenuStat(val label: String, val value: String, val accent: Color)

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onHowToPlay: () -> Unit,
    onSettings: () -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = onPlay,
        onHowToPlay = onHowToPlay,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onHowToPlay: () -> Unit,
    onSettings: () -> Unit
) {
    val labelBest = stringResource(R.string.stat_best)
    val labelStreak = stringResource(R.string.stat_streak)
    val labelRuns = stringResource(R.string.stat_runs)
    val labelDrops = stringResource(R.string.stat_drops)
    val labelTier = stringResource(R.string.stat_tier)
    val tierValue = stringResource(SampleData.tierLabel(state.tier))

    val topStats = ArrayList<MenuStat>(2)
    if (state.bestScore > 0) {
        topStats.add(MenuStat(labelBest, formatCharge(state.bestScore), AccentCyan))
    }
    if (state.bestStreak > 0) {
        topStats.add(MenuStat(labelStreak, state.bestStreak.toString(), AccentAmber))
    }

    val bottomStats = ArrayList<MenuStat>(3)
    if (state.runsCompleted > 0) {
        bottomStats.add(MenuStat(labelRuns, state.runsCompleted.toString(), AccentViolet))
    }
    if (state.lifetimeDrops > 0) {
        bottomStats.add(MenuStat(labelDrops, state.lifetimeDrops.toString(), AccentMagenta))
    }
    bottomStats.add(MenuStat(labelTier, tierValue, AccentCyan))

    Box(modifier = Modifier.fillMaxSize()) {
        CircuitBackground(
            overlay = MenuOverlay,
            bloom = AccentCyan.copy(alpha = 0.20f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            MenuHeader()
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlayTile(
                    reducedMotion = state.reducedMotion,
                    onPlay = onPlay,
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (topStats.size >= 2) {
                        topStats.forEach { stat ->
                            StatCard(
                                label = stat.label,
                                value = stat.value,
                                accent = stat.accent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    } else {
                        HudCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            corner = 16.dp
                        ) {
                            Text(
                                text = stringResource(R.string.no_runs_yet),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 10.dp)
                            )
                        }
                    }
                }
            }
            if (bottomStats.size >= 2) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    bottomStats.forEach { stat ->
                        StatCard(
                            label = stat.label,
                            value = stat.value,
                            accent = stat.accent,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    label = stringResource(R.string.how_to_play),
                    onClick = onHowToPlay,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    label = stringResource(R.string.settings),
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MenuHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.cd_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )
        HudCard(
            modifier = Modifier.size(44.dp),
            corner = 14.dp
        ) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun PlayTile(
    reducedMotion: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pulse = remember { Animatable(1f) }
    val sheen = remember { Animatable(0f) }
    LaunchedEffect(reducedMotion) {
        if (!reducedMotion) {
            pulse.animateTo(
                targetValue = 1.06f,
                animationSpec = repeatable(
                    iterations = 4,
                    animation = tween(durationMillis = 900, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            pulse.snapTo(1f)
        }
    }
    LaunchedEffect(reducedMotion) {
        if (!reducedMotion) {
            sheen.animateTo(
                targetValue = 1f,
                animationSpec = repeatable(
                    iterations = 2,
                    animation = tween(durationMillis = 2000, easing = LinearEasing)
                )
            )
            sheen.snapTo(0f)
        }
    }
    val tokenScale = pulse.value
    val shape = RoundedCornerShape(22.dp)
    Button(
        onClick = onPlay,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        elevation = null,
        border = BorderStroke(1.5.dp, AccentViolet.copy(alpha = 0.55f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceCircuit.copy(alpha = 0.82f)
        ),
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!reducedMotion) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .graphicsLayer { translationY = sheen.value * 240f }
                        .background(AccentCyan.copy(alpha = 0.28f))
                )
            }
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(R.string.cd_token),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 26.dp)
                    .size(76.dp)
                    .graphicsLayer {
                        scaleX = tokenScale
                        scaleY = tokenScale
                    }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(AccentViolet, AccentCyan))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.play),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }
        }
    }
}
