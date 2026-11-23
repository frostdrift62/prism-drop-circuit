package com.Korgaga1.Zonkow.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.domain.model.BoardLayout
import com.Korgaga1.Zonkow.domain.model.DropOutcome
import com.Korgaga1.Zonkow.domain.model.GamePhase
import com.Korgaga1.Zonkow.domain.model.RunResult
import com.Korgaga1.Zonkow.domain.model.Tier
import com.Korgaga1.Zonkow.domain.repository.ProgressRepository
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository
import com.Korgaga1.Zonkow.domain.usecase.BuildPatternUseCase
import com.Korgaga1.Zonkow.domain.usecase.GenerateBoardUseCase
import com.Korgaga1.Zonkow.domain.usecase.ScoreDropUseCase
import com.Korgaga1.Zonkow.domain.usecase.SimulateDropUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.min

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val generateBoard: GenerateBoardUseCase,
    private val simulateDrop: SimulateDropUseCase,
    private val buildPattern: BuildPatternUseCase,
    private val scoreDrop: ScoreDropUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var fallJob: Job? = null
    private var patternJob: Job? = null
    private var feedbackJob: Job? = null
    private var layout: BoardLayout? = null
    private var runIndex: Int = 0
    private var runActive: Boolean = false
    private var tier: Tier = Tier.Standard

    fun prepareRun() {
        if (runActive) {
            return
        }
        runActive = true
        cancelJobs()
        val settings = settingsRepository.settings()
        tier = settings.tier
        runIndex = progressRepository.nextRunIndex()
        val board = generateBoard(runIndex, 0, tier.moveBudget)
        layout = board
        _state.value = GameUiState(
            phase = GamePhase.Routing,
            tier = tier,
            dropIndex = 0,
            movesLeft = tier.moveBudget,
            moveBudget = tier.moveBudget,
            gates = board.gates,
            targetLane = board.targetLane,
            voidLane = board.voidLane,
            tokenLane = GameConfig.START_LANE,
            highContrastMarks = settings.highContrastMarks,
            reducedMotion = settings.reducedMotion
        )
    }

    fun abandonRun() {
        cancelJobs()
        runActive = false
    }

    fun pause() {
        cancelJobs()
        val phase = _state.value.phase
        if (phase == GamePhase.Falling || phase == GamePhase.Landing || phase == GamePhase.Pattern) {
            _state.update { it.copy(paused = true) }
        }
    }

    fun requestPause() {
        pause()
    }

    fun resume() {
        val current = _state.value
        if (!current.paused) {
            return
        }
        _state.update { it.copy(paused = false) }
        when (current.phase) {
            GamePhase.Falling -> startFall()
            GamePhase.Landing -> continueAfterLanding()
            GamePhase.Pattern -> startPattern(current.dropIndex)
            else -> Unit
        }
    }

    fun onGateFlip(index: Int) {
        val current = _state.value
        if (!current.gatesEnabled) {
            return
        }
        _state.update { snapshot ->
            snapshot.copy(
                gates = snapshot.gates.map { gate ->
                    if (gate.index == index) gate.copy(facingRight = !gate.facingRight) else gate
                },
                movesLeft = snapshot.movesLeft - 1
            )
        }
    }

    fun onGateSlide(index: Int, laneDelta: Int) {
        val current = _state.value
        if (!current.gatesEnabled || laneDelta == 0) {
            return
        }
        val gate = current.gates.firstOrNull { it.index == index } ?: return
        val target = (gate.lane + laneDelta).coerceIn(0, GameConfig.COLS - 1)
        if (target == gate.lane) {
            return
        }
        _state.update { snapshot ->
            snapshot.copy(
                gates = snapshot.gates.map { item ->
                    if (item.index == index) item.copy(lane = target) else item
                },
                movesLeft = snapshot.movesLeft - 1
            )
        }
    }

    fun onDrop() {
        if (!_state.value.canDrop) {
            return
        }
        startFall()
    }

    fun onPrismTap(tileIndex: Int) {
        val current = _state.value
        if (current.phase != GamePhase.Pattern || !current.patternAcceptingInput || current.paused) {
            return
        }
        val expected = current.patternSequence.getOrNull(current.patternInputIndex) ?: return
        if (tileIndex == expected) {
            val nextIndex = current.patternInputIndex + 1
            flashTile(tileIndex, true)
            if (nextIndex >= current.patternSequence.size) {
                patternJob?.cancel()
                _state.update { it.copy(patternAcceptingInput = false) }
                onPatternSolved()
            } else {
                _state.update { it.copy(patternInputIndex = nextIndex) }
            }
        } else {
            patternJob?.cancel()
            flashTile(tileIndex, false)
            _state.update { it.copy(patternAcceptingInput = false) }
            onPatternFailed()
        }
    }

    private fun flashTile(tileIndex: Int, correct: Boolean) {
        feedbackJob?.cancel()
        _state.update { it.copy(tapFeedbackTile = tileIndex, tapFeedbackCorrect = correct) }
        feedbackJob = viewModelScope.launch {
            delay(GameConfig.TAP_FLASH_MS)
            _state.update { it.copy(tapFeedbackTile = -1) }
        }
    }

    private fun startFall() {
        val board = layout ?: return
        fallJob?.cancel()
        patternJob?.cancel()
        val outcome = simulateDrop(board, _state.value.gates, GameConfig.START_LANE)
        val stepMs = (GameConfig.FALL_STEP_MS / _state.value.tier.fallSpeed).toLong().coerceAtLeast(40L)
        val rows = (outcome.path.size - 1).toFloat()
        _state.update {
            it.copy(
                phase = GamePhase.Falling,
                path = outcome.path,
                fallProgress = 0f,
                tokenLane = outcome.path.first(),
                landedLane = -1,
                landedOnTarget = false,
                landedOnVoid = false,
                paused = false
            )
        }
        fallJob = viewModelScope.launch {
            var elapsed = 0L
            val total = stepMs * (outcome.path.size - 1)
            while (isActive && elapsed < total) {
                delay(GameConfig.FRAME_MS)
                elapsed += GameConfig.FRAME_MS
                val progress = (elapsed.toFloat() / stepMs).coerceIn(0f, rows)
                _state.update { it.copy(fallProgress = progress) }
            }
            _state.update { it.copy(fallProgress = rows, tokenLane = outcome.finalLane) }
            resolveLanding(outcome)
        }
    }

    private suspend fun resolveLanding(outcome: DropOutcome) {
        val gained = scoreDrop(outcome, _state.value.amplifier)
        _state.update { snapshot ->
            snapshot.copy(
                phase = GamePhase.Landing,
                score = snapshot.score + gained,
                lastGain = gained,
                integrity = if (outcome.hitVoid) snapshot.integrity - 1 else snapshot.integrity,
                targetsHit = if (outcome.hitTarget) snapshot.targetsHit + 1 else snapshot.targetsHit,
                dropsCompleted = snapshot.dropsCompleted + 1,
                landedLane = outcome.finalLane,
                landedOnTarget = outcome.hitTarget,
                landedOnVoid = outcome.hitVoid
            )
        }
        delay(GameConfig.LANDING_HOLD_MS)
        continueAfterLanding()
    }

    private fun continueAfterLanding() {
        val current = _state.value
        if (current.integrity <= 0) {
            finishRun(burnedOut = true)
            return
        }
        if (current.dropIndex + 1 >= GameConfig.DROPS_PER_RUN) {
            finishRun(burnedOut = false)
            return
        }
        startPattern(current.dropIndex + 1)
    }

    private fun startPattern(dropIndex: Int) {
        patternJob?.cancel()
        val challenge = buildPattern(runIndex, dropIndex, tier)
        _state.update {
            it.copy(
                phase = GamePhase.Pattern,
                dropIndex = dropIndex,
                patternSequence = challenge.sequence,
                patternRevealIndex = -1,
                patternInputIndex = 0,
                patternAcceptingInput = false,
                patternTimeLeft = 1f,
                tapFeedbackTile = -1,
                paused = false
            )
        }
        patternJob = viewModelScope.launch {
            delay(GameConfig.REVEAL_GAP_MS)
            for (step in challenge.sequence.indices) {
                _state.update { it.copy(patternRevealIndex = step) }
                delay(GameConfig.REVEAL_ON_MS)
                _state.update { it.copy(patternRevealIndex = -1) }
                delay(GameConfig.REVEAL_GAP_MS)
            }
            _state.update { it.copy(patternAcceptingInput = true) }
            var remaining = challenge.timerMs
            while (isActive && remaining > 0 && _state.value.patternAcceptingInput) {
                delay(GameConfig.FRAME_MS)
                remaining -= GameConfig.FRAME_MS
                val fraction = (remaining.toFloat() / challenge.timerMs).coerceIn(0f, 1f)
                _state.update { it.copy(patternTimeLeft = fraction) }
            }
            if (isActive && _state.value.patternAcceptingInput) {
                _state.update { it.copy(patternAcceptingInput = false, patternTimeLeft = 0f) }
                onPatternTimeout()
            }
        }
    }

    private fun onPatternSolved() {
        val streak = _state.value.streak + 1
        val amplifier = min(
            GameConfig.AMPLIFIER_BASE + GameConfig.AMPLIFIER_STEP * streak,
            GameConfig.AMPLIFIER_MAX
        )
        _state.update {
            it.copy(
                streak = streak,
                bestStreak = maxOf(it.bestStreak, streak),
                amplifier = amplifier
            )
        }
        scheduleNextDrop()
    }

    private fun onPatternFailed() {
        _state.update {
            it.copy(
                streak = 0,
                amplifier = GameConfig.AMPLIFIER_BASE,
                integrity = it.integrity - 1
            )
        }
        if (_state.value.integrity <= 0) {
            finishRun(burnedOut = true)
            return
        }
        scheduleNextDrop()
    }

    private fun onPatternTimeout() {
        _state.update {
            it.copy(
                streak = 0,
                amplifier = GameConfig.AMPLIFIER_BASE,
                integrity = it.integrity - 1
            )
        }
        if (_state.value.integrity <= 0) {
            finishRun(burnedOut = true)
            return
        }
        scheduleNextDrop()
    }

    private fun scheduleNextDrop() {
        patternJob?.cancel()
        patternJob = viewModelScope.launch {
            delay(GameConfig.FEEDBACK_MS)
            armNextDrop()
        }
    }

    private fun armNextDrop() {
        val current = _state.value
        val board = generateBoard(runIndex, current.dropIndex, current.moveBudget)
        layout = board
        _state.update {
            it.copy(
                phase = GamePhase.Routing,
                gates = board.gates,
                targetLane = board.targetLane,
                voidLane = board.voidLane,
                movesLeft = it.moveBudget,
                path = emptyList(),
                fallProgress = 0f,
                tokenLane = GameConfig.START_LANE,
                landedLane = -1,
                landedOnTarget = false,
                landedOnVoid = false,
                patternSequence = emptyList(),
                patternRevealIndex = -1,
                patternInputIndex = 0,
                patternAcceptingInput = false,
                tapFeedbackTile = -1
            )
        }
    }

    private fun finishRun(burnedOut: Boolean) {
        if (!runActive) {
            return
        }
        cancelJobs()
        runActive = false
        val current = _state.value
        val newBest = progressRepository.recordRun(
            score = current.score,
            drops = current.dropsCompleted,
            streak = current.bestStreak
        )
        _state.update { it.copy(phase = GamePhase.Finished, paused = false) }
        val result = RunResult(
            score = current.score,
            drops = current.dropsCompleted,
            bestStreak = current.bestStreak,
            targetsHit = current.targetsHit,
            burnedOut = burnedOut,
            newBest = newBest
        )
        eventChannel.trySend(GameEvent.Finished(result))
    }

    private fun cancelJobs() {
        fallJob?.cancel()
        patternJob?.cancel()
        feedbackJob?.cancel()
        fallJob = null
        patternJob = null
        feedbackJob = null
    }

    override fun onCleared() {
        cancelJobs()
        super.onCleared()
    }
}
