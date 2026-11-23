package com.Korgaga1.Zonkow.domain.model

enum class Tier(
    val id: String,
    val patternBase: Int,
    val patternMax: Int,
    val moveBudget: Int,
    val patternTimerMs: Long,
    val fallSpeed: Float
) {
    Calm("calm", 3, 3, 4, 7000L, 0.8f),
    Standard("standard", 3, 5, 3, 6000L, 1.0f),
    Surge("surge", 4, 6, 2, 4500L, 1.3f);

    companion object {
        fun fromId(id: String): Tier = Tier.entries.firstOrNull { it.id == id } ?: Standard
    }
}
