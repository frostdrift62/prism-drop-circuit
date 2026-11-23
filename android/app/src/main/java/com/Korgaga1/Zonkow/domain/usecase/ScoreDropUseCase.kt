package com.Korgaga1.Zonkow.domain.usecase

import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.domain.model.DropOutcome
import kotlin.math.roundToInt

class ScoreDropUseCase {

    operator fun invoke(outcome: DropOutcome, amplifier: Float): Int {
        if (outcome.hitVoid) {
            return 0
        }
        val base = (outcome.laneValue * amplifier).roundToInt()
        return if (outcome.hitTarget) base * GameConfig.TARGET_MULTIPLIER else base
    }
}
