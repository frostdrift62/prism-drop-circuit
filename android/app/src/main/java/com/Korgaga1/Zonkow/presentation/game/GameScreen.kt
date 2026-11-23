package com.Korgaga1.Zonkow.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Korgaga1.Zonkow.R
import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.core.di.ServiceLocator
import com.Korgaga1.Zonkow.core.theme.AccentAmber
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.AccentViolet
import com.Korgaga1.Zonkow.core.theme.InertLane
import com.Korgaga1.Zonkow.core.theme.PrismPalette
import com.Korgaga1.Zonkow.core.theme.SurfaceElevated
import com.Korgaga1.Zonkow.core.theme.TextMuted
import com.Korgaga1.Zonkow.core.theme.TextPrimary
import com.Korgaga1.Zonkow.core.theme.TextSecondary
import com.Korgaga1.Zonkow.core.ui.CircuitBackground
import com.Korgaga1.Zonkow.core.ui.GateSprite
import com.Korgaga1.Zonkow.core.ui.HudCard
import com.Korgaga1.Zonkow.core.ui.IntegrityRow
import com.Korgaga1.Zonkow.core.ui.PrimaryButton
import com.Korgaga1.Zonkow.core.ui.PrismTile
import com.Korgaga1.Zonkow.core.ui.SecondaryButton
import com.Korgaga1.Zonkow.core.ui.formatCharge
import com.Korgaga1.Zonkow.data.sample.SampleData
import com.Korgaga1.Zonkow.domain.model.GamePhase
import com.Korgaga1.Zonkow.domain.model.RunResult

private val GameOverlay = listOf(
    Color(0xCC0A0014),
    Color(0xA60D1B2A),
    Color(0xE60A0014)
)

@Composable
fun GameScreen(
    onExit: () -> Unit,
    onFinished: (RunResult) -> Unit
) {
    val viewModel: GameViewModel = viewModel(factory = ServiceLocator.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.prepareRun()
    }
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is GameEvent.Finished) {
                onFinished(event.result)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }
    BackHandler {
        viewModel.abandonRun()
        onExit()
    }

    GameContent(
        state = state,
        onHome = {
            viewModel.abandonRun()
            onExit()
        },
        onPause = viewModel::requestPause,
        onResume = viewModel::resume,
        onDrop = viewModel::onDrop,
        onGateFlip = viewModel::onGateFlip,
        onGateSlide = viewModel::onGateSlide,
        onPrismTap = viewModel::onPrismTap
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onHome: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDrop: () -> Unit,
    onGateFlip: (Int) -> Unit,
    onGateSlide: (Int, Int) -> Unit,
    onPrismTap: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CircuitBackground(
            overlay = GameOverlay,
            bloom = AccentViolet.copy(alpha = 0.14f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 12.dp)
        ) {
            GameHeader(state = state, onHome = onHome, onPause = onPause)
            PhaseStrip(state = state)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                CircuitBoard(
                    state = state,
                    onGateFlip = onGateFlip,
                    onGateSlide = onGateSlide,
                    modifier = Modifier.fillMaxSize()
                )
                if (state.phase == GamePhase.Pattern) {
                    PatternPanel(
                        state = state,
                        onPrismTap = onPrismTap,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    )
                }
                if (state.paused) {
                    PausePanel(
                        onResume = onResume,
                        onMenu = onHome,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            ControlBar(state = state, onDrop = onDrop)
        }
    }
}

@Composable
private fun GameHeader(
    state: GameUiState,
    onHome: () -> Unit,
    onPause: () -> Unit
) {
    val integrityState = stringResource(
        R.string.state_integrity,
        state.integrity,
        GameConfig.INTEGRITY_MAX
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconTile(
            image = R.drawable.icon_home,
            description = stringResource(R.string.cd_home),
            onClick = onHome
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    R.string.drop_counter,
                    state.dropsLabel,
                    GameConfig.DROPS_PER_RUN
                ),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            IntegrityRow(
                charges = state.integrity,
                maximum = GameConfig.INTEGRITY_MAX,
                description = integrityState,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
        IconTile(
            image = R.drawable.icon_pause,
            description = stringResource(R.string.cd_pause),
            onClick = onPause
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatCharge(state.score),
                style = MaterialTheme.typography.headlineSmall,
                color = AccentCyan,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Text(
                text = stringResource(R.string.amplifier_format, state.amplifier),
                style = MaterialTheme.typography.labelMedium,
                color = AccentViolet
            )
        }
    }
}

@Composable
private fun IconTile(
    image: Int,
    description: String,
    onClick: () -> Unit
) {
    HudCard(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        corner = 14.dp
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun PhaseStrip(state: GameUiState) {
    val message = when {
        state.paused -> stringResource(R.string.paused)
        state.phase == GamePhase.Routing -> stringResource(R.string.control_hint)
        state.phase == GamePhase.Falling -> stringResource(R.string.phase_falling)
        state.phase == GamePhase.Landing -> landingMessage(state)
        state.phase == GamePhase.Pattern ->
            if (state.patternAcceptingInput) {
                stringResource(R.string.repeat_pattern)
            } else {
                stringResource(R.string.watch_pattern)
            }

        else -> stringResource(R.string.phase_falling)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun landingMessage(state: GameUiState): String = when {
    state.landedOnVoid -> stringResource(R.string.landing_void)
    state.landedOnTarget -> stringResource(R.string.landing_target)
    else -> stringResource(R.string.landing_plain, state.lastGain)
}

@Composable
private fun CircuitBoard(
    state: GameUiState,
    onGateFlip: (Int) -> Unit,
    onGateSlide: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val frame = 8.dp
        val rowsTotal = GameConfig.BOARD_ROWS + 1
        val widthLimit = minOf(maxWidth - 8.dp, 380.dp)
        val cellByWidth = (widthLimit - frame * 2) / GameConfig.COLS
        val cellByHeight = (maxHeight - frame * 2 - 6.dp) / rowsTotal
        val cell = minOf(cellByWidth, cellByHeight)
        val boardWidth = cell * GameConfig.COLS + frame * 2
        val boardHeight = cell * rowsTotal + frame * 2
        val cellPx = with(LocalDensity.current) { cell.toPx() }

        HudCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(boardWidth)
                .height(boardHeight),
            corner = 18.dp,
            container = Color(0xC7120026)
        ) {
            PegField(cell = cell, frame = frame, activeRow = state.fallProgress.toInt())
            LaneStrip(state = state, cell = cell, frame = frame)
            if (state.tokenOnBoard) {
                TokenTrail(state = state, cell = cell, frame = frame)
                FallingToken(state = state, cell = cell, frame = frame)
            }
            state.gates.forEach { gate ->
                val offsetX by animateDpAsState(
                    targetValue = frame + cell * gate.lane,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f)
                )
                val row = GameConfig.GATE_ROWS.getOrElse(gate.index) { 0 }
                GateSprite(
                    index = gate.index,
                    facingRight = gate.facingRight,
                    enabled = state.gatesEnabled,
                    description = stringResource(R.string.cd_gate),
                    state = stringResource(
                        R.string.state_gate,
                        gate.index + 1,
                        gate.lane + 1,
                        stringResource(
                            if (gate.facingRight) {
                                R.string.facing_right
                            } else {
                                R.string.facing_left
                            }
                        )
                    ),
                    hitSize = cell,
                    spriteSize = cell * 0.9f,
                    laneWidthPx = cellPx,
                    onFlip = onGateFlip,
                    onSlide = onGateSlide,
                    modifier = Modifier.offset(x = offsetX, y = frame + cell * row)
                )
            }
        }
    }
}

@Composable
private fun PegField(cell: Dp, frame: Dp, activeRow: Int) {
    val pegSize = cell * 0.34f
    for (row in 0 until GameConfig.BOARD_ROWS) {
        if (GameConfig.GATE_ROWS.contains(row)) {
            continue
        }
        val even = row % 2 == 0
        val count = if (even) GameConfig.COLS else GameConfig.COLS - 1
        val shift = if (even) 0f else 0.5f
        val lit = row == activeRow
        for (index in 0 until count) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(
                        x = frame + cell * (index + shift) + (cell - pegSize) / 2,
                        y = frame + cell * row + (cell - pegSize) / 2
                    )
                    .size(pegSize)
                    .alpha(if (lit) 1f else 0.55f)
            )
        }
    }
}

@Composable
private fun LaneStrip(state: GameUiState, cell: Dp, frame: Dp) {
    val stripTop = frame + cell * GameConfig.BOARD_ROWS
    for (lane in 0 until GameConfig.COLS) {
        val isTarget = lane == state.targetLane
        val isVoid = lane == state.voidLane
        val landed = lane == state.landedLane
        val tint = when {
            isTarget -> AccentCyan.copy(alpha = if (landed) 0.55f else 0.24f)
            isVoid -> InertLane.copy(alpha = 0.9f)
            else -> SurfaceElevated.copy(alpha = 0.55f)
        }
        Box(
            modifier = Modifier
                .offset(x = frame + cell * lane, y = stripTop)
                .width(cell)
                .height(cell)
                .padding(1.dp)
                .background(color = tint, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isTarget) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = stringResource(R.string.cd_beacon),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(cell * 0.42f)
                )
            }
            Text(
                text = if (isVoid) {
                    stringResource(R.string.lane_void)
                } else {
                    GameConfig.LANE_VALUES[lane].toString()
                },
                style = MaterialTheme.typography.labelMedium,
                color = when {
                    isVoid -> TextMuted
                    isTarget -> AccentCyan
                    else -> TextSecondary
                },
                maxLines = 1,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun FallingToken(state: GameUiState, cell: Dp, frame: Dp) {
    val tokenSize = cell * 0.6f
    val lane = interpolatedLane(state)
    val progress = if (state.phase == GamePhase.Routing) 0f else state.fallProgress
    Image(
        painter = painterResource(R.drawable.sprite_1),
        contentDescription = stringResource(R.string.cd_token),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .offset(
                x = frame + cell * lane + (cell - tokenSize) / 2,
                y = frame + cell * progress + (cell - tokenSize) / 2
            )
            .size(tokenSize)
    )
}

@Composable
private fun TokenTrail(state: GameUiState, cell: Dp, frame: Dp) {
    if (state.reducedMotion || state.phase != GamePhase.Falling) {
        return
    }
    val dotSize = cell * 0.22f
    val path = state.path
    if (path.isEmpty()) {
        return
    }
    for (segment in 1..GameConfig.TRAIL_SEGMENTS) {
        val position = state.fallProgress - segment * 0.26f
        if (position < 0f) {
            continue
        }
        val index = position.toInt().coerceIn(0, path.size - 1)
        val fraction = position - index
        val laneA = path[index]
        val laneB = path.getOrElse(index + 1) { laneA }
        val lane = laneA + (laneB - laneA) * fraction
        Box(
            modifier = Modifier
                .offset(
                    x = frame + cell * lane + (cell - dotSize) / 2,
                    y = frame + cell * position + (cell - dotSize) / 2
                )
                .size(dotSize)
                .alpha(0.55f - segment * 0.08f)
                .background(color = AccentCyan, shape = CircleShape)
        )
    }
}

private fun interpolatedLane(state: GameUiState): Float {
    val path = state.path
    if (path.isEmpty() || state.phase == GamePhase.Routing) {
        return state.tokenLane.toFloat()
    }
    val index = state.fallProgress.toInt().coerceIn(0, path.size - 1)
    val fraction = state.fallProgress - index
    val laneA = path[index]
    val laneB = path.getOrElse(index + 1) { laneA }
    return laneA + (laneB - laneA) * fraction
}

@Composable
private fun PatternPanel(
    state: GameUiState,
    onPrismTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    HudCard(
        modifier = modifier.padding(horizontal = 4.dp),
        corner = 22.dp,
        container = Color(0xF21A0038)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = stringResource(R.string.cd_shard),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = if (state.patternAcceptingInput) {
                        stringResource(R.string.repeat_pattern)
                    } else {
                        stringResource(R.string.watch_pattern)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
                Text(
                    text = stringResource(
                        R.string.pattern_progress,
                        state.patternInputIndex,
                        state.patternSequence.size
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentCyan
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(4.dp)
                    .background(color = InertLane, shape = RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.patternTimeLeft)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(listOf(AccentViolet, AccentCyan)),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                for (tile in 0 until GameConfig.PRISM_COLORS) {
                    val markRes = SampleData.prismMarks[tile]
                    val mark = stringResource(markRes)
                    PrismTile(
                        accent = PrismPalette[tile],
                        mark = mark,
                        label = stringResource(R.string.cd_prism_tile, mark),
                        state = stringResource(
                            if (state.patternAcceptingInput) {
                                R.string.state_tile_ready
                            } else {
                                R.string.state_tile_locked
                            }
                        ),
                        flashing = state.patternRevealIndex >= 0 &&
                            state.patternSequence.getOrNull(state.patternRevealIndex) == tile ||
                            state.tapFeedbackTile == tile,
                        highContrast = state.highContrastMarks,
                        enabled = state.patternAcceptingInput,
                        reducedMotion = state.reducedMotion,
                        onTap = { onPrismTap(tile) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PausePanel(
    onResume: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    HudCard(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .width(260.dp),
        corner = 22.dp,
        container = Color(0xF21A0038)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.paused),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            PrimaryButton(
                label = stringResource(R.string.resume),
                onClick = onResume,
                height = 54.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
            )
            SecondaryButton(
                label = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun ControlBar(state: GameUiState, onDrop: () -> Unit) {
    HudCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp),
        corner = 20.dp,
        container = Color(0xDB1A0038)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HudChip(
                    label = stringResource(
                        R.string.moves_counter,
                        state.movesLeft,
                        state.moveBudget
                    ),
                    accent = if (state.movesLeft > 0) AccentAmber else TextMuted
                )
                Spacer(modifier = Modifier.weight(1f))
                HudChip(
                    label = stringResource(R.string.target_lane, state.targetLane + 1),
                    accent = AccentCyan
                )
            }
            PrimaryButton(
                label = stringResource(R.string.drop),
                onClick = onDrop,
                enabled = state.canDrop,
                height = 56.dp,
                disabledState = stringResource(R.string.state_drop_disabled),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun HudChip(label: String, accent: Color) {
    Box(
        modifier = Modifier
            .background(
                color = accent.copy(alpha = 0.14f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            maxLines = 1
        )
    }
}
