package com.Korgaga1.Zonkow.domain.usecase

import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.domain.model.PatternChallenge
import com.Korgaga1.Zonkow.domain.model.Tier
import kotlin.math.min
import kotlin.random.Random

class BuildPatternUseCase {

    operator fun invoke(runIndex: Int, dropIndex: Int, tier: Tier): PatternChallenge {
        val bracket = when {
            dropIndex < 3 -> 0
            dropIndex < 6 -> 1
            else -> 2
        }
        val length = min(tier.patternBase + bracket, tier.patternMax)
        val random = Random((runIndex * 31337) xor ((dropIndex + 7) * 15485863))
        val sequence = ArrayList<Int>(length)
        var previous = -1
        repeat(length) {
            var pick = random.nextInt(GameConfig.PRISM_COLORS)
            if (pick == previous) {
                pick = (pick + 1) % GameConfig.PRISM_COLORS
            }
            previous = pick
            sequence.add(pick)
        }
        return PatternChallenge(sequence = sequence, timerMs = tier.patternTimerMs)
    }
}
