package com.Korgaga1.Zonkow.core.config

object GameConfig {
    const val LOADER_DURATION_MS: Long = 8000L
    const val DROPS_PER_RUN: Int = 8
    const val COLS: Int = 7
    const val BOARD_ROWS: Int = 9
    const val START_LANE: Int = 3
    const val INTEGRITY_MAX: Int = 3
    const val AMPLIFIER_BASE: Float = 1.0f
    const val AMPLIFIER_STEP: Float = 0.5f
    const val AMPLIFIER_MAX: Float = 4.0f
    const val TARGET_MULTIPLIER: Int = 2
    const val PRISM_COLORS: Int = 4
    const val FRAME_MS: Long = 16L
    const val FALL_STEP_MS: Long = 150L
    const val LANDING_HOLD_MS: Long = 850L
    const val REVEAL_ON_MS: Long = 420L
    const val REVEAL_GAP_MS: Long = 160L
    const val FEEDBACK_MS: Long = 520L
    const val TAP_FLASH_MS: Long = 140L
    const val TRAIL_SEGMENTS: Int = 6
    const val GENERATOR_ATTEMPTS: Int = 20

    val LANE_VALUES: List<Int> = listOf(10, 25, 60, 120, 60, 25, 10)
    val GATE_ROWS: List<Int> = listOf(2, 5, 8)
}
