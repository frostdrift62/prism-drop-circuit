package com.Korgaga1.Zonkow.data.sample

import com.Korgaga1.Zonkow.R
import com.Korgaga1.Zonkow.domain.model.HowToStep
import com.Korgaga1.Zonkow.domain.model.Tier

object SampleData {

    val howToSteps: List<HowToStep> = listOf(
        HowToStep(
            id = 1,
            imageRes = R.drawable.sprite_2,
            titleRes = R.string.how_gates_title,
            textRes = R.string.how_gates_text
        ),
        HowToStep(
            id = 2,
            imageRes = R.drawable.sprite_1,
            titleRes = R.string.how_drop_title,
            textRes = R.string.how_drop_text
        ),
        HowToStep(
            id = 3,
            imageRes = R.drawable.sprite_5,
            titleRes = R.string.how_lanes_title,
            textRes = R.string.how_lanes_text
        ),
        HowToStep(
            id = 4,
            imageRes = R.drawable.sprite_4,
            titleRes = R.string.how_pattern_title,
            textRes = R.string.how_pattern_text
        )
    )

    fun tierLabel(tier: Tier): Int = when (tier) {
        Tier.Calm -> R.string.tier_calm
        Tier.Standard -> R.string.tier_standard
        Tier.Surge -> R.string.tier_surge
    }

    fun tierDetail(tier: Tier): Int = when (tier) {
        Tier.Calm -> R.string.tier_calm_detail
        Tier.Standard -> R.string.tier_standard_detail
        Tier.Surge -> R.string.tier_surge_detail
    }

    val prismMarks: List<Int> = listOf(
        R.string.mark_star,
        R.string.mark_check,
        R.string.mark_dot,
        R.string.mark_heart
    )
}
