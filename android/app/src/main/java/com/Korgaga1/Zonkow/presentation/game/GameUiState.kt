package com.Korgaga1.Zonkow.presentation.game

import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.domain.model.GamePhase
import com.Korgaga1.Zonkow.domain.model.GateState
import com.Korgaga1.Zonkow.domain.model.RunResult
import com.Korgaga1.Zonkow.domain.model.Tier

data class GameUiState(
    val phase: GamePhase = GamePhase.Routing,
    val tier: Tier = Tier.Standard,
    val dropIndex: Int = 0,
    val dropsCompleted: Int = 0,
    val score: Int = 0,
    val lastGain: Int = 0,
    val integrity: Int = GameConfig.INTEGRITY_MAX,
    val amplifier: Float = GameConfig.AMPLIFIER_BASE,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val targetsHit: Int = 0,
    val movesLeft: Int = GameConfig.INTEGRITY_MAX,
    val moveBudget: Int = GameConfig.INTEGRITY_MAX,
    val gates: List<GateState> = emptyList(),
    val targetLane: Int = GameConfig.START_LANE,
    val voidLane: Int = 0,
    val path: List<Int> = emptyList(),
    val fallProgress: Float = 0f,
    val tokenLane: Int = GameConfig.START_LANE,
    val landedLane: Int = -1,
    val landedOnTarget: Boolean = false,
    val landedOnVoid: Boolean = false,
    val patternSequence: List<Int> = emptyList(),
    val patternRevealIndex: Int = -1,
    val patternInputIndex: Int = 0,
    val patternAcceptingInput: Boolean = false,
    val patternTimeLeft: Float = 1f,
    val tapFeedbackTile: Int = -1,
    val tapFeedbackCorrect: Boolean = true,
    val paused: Boolean = false,
    val highContrastMarks: Boolean = false,
    val reducedMotion: Boolean = false
) {
    val dropsLabel: Int
        get() = dropIndex + 1

    val canDrop: Boolean
        get() = phase == GamePhase.Routing && !paused

    val gatesEnabled: Boolean
        get() = phase == GamePhase.Routing && movesLeft > 0 && !paused

    val tokenOnBoard: Boolean
        get() = phase == GamePhase.Routing || phase == GamePhase.Falling || phase == GamePhase.Landing
}

sealed interface GameEvent {
    data class Finished(val result: RunResult) : GameEvent
}
