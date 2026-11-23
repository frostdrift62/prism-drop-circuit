package com.Korgaga1.Zonkow.domain.model

data class RunResult(
    val score: Int,
    val drops: Int,
    val bestStreak: Int,
    val targetsHit: Int,
    val burnedOut: Boolean,
    val newBest: Boolean
)
