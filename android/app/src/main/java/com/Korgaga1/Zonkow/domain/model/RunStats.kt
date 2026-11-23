package com.Korgaga1.Zonkow.domain.model

data class RunStats(
    val bestScore: Int,
    val bestStreak: Int,
    val runsCompleted: Int,
    val lifetimeDrops: Int
) {
    val hasHistory: Boolean
        get() = runsCompleted > 0 || bestScore > 0 || lifetimeDrops > 0
}
