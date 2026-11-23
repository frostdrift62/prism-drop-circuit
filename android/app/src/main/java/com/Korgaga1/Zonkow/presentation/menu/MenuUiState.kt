package com.Korgaga1.Zonkow.presentation.menu

import com.Korgaga1.Zonkow.domain.model.Tier

data class MenuUiState(
    val bestScore: Int = 0,
    val bestStreak: Int = 0,
    val runsCompleted: Int = 0,
    val lifetimeDrops: Int = 0,
    val tier: Tier = Tier.Standard,
    val reducedMotion: Boolean = false,
    val hasHistory: Boolean = false
)
