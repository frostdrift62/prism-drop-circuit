package com.Korgaga1.Zonkow.data.repository

import com.Korgaga1.Zonkow.data.local.PrismPreferences
import com.Korgaga1.Zonkow.domain.model.RunStats
import com.Korgaga1.Zonkow.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val preferences: PrismPreferences) : ProgressRepository {

    override fun stats(): RunStats = RunStats(
        bestScore = preferences.readInt(PrismPreferences.KEY_BEST_SCORE, 0),
        bestStreak = preferences.readInt(PrismPreferences.KEY_BEST_STREAK, 0),
        runsCompleted = preferences.readInt(PrismPreferences.KEY_RUNS, 0),
        lifetimeDrops = preferences.readInt(PrismPreferences.KEY_DROPS, 0)
    )

    override fun nextRunIndex(): Int {
        val next = preferences.readInt(PrismPreferences.KEY_RUN_INDEX, 0) + 1
        preferences.writeInt(PrismPreferences.KEY_RUN_INDEX, next)
        return next
    }

    override fun recordRun(score: Int, drops: Int, streak: Int): Boolean {
        val current = stats()
        val isBest = score > current.bestScore
        if (isBest) {
            preferences.writeInt(PrismPreferences.KEY_BEST_SCORE, score)
        }
        if (streak > current.bestStreak) {
            preferences.writeInt(PrismPreferences.KEY_BEST_STREAK, streak)
        }
        preferences.writeInt(PrismPreferences.KEY_RUNS, current.runsCompleted + 1)
        preferences.writeInt(PrismPreferences.KEY_DROPS, current.lifetimeDrops + drops)
        return isBest
    }

    override fun resetProgress() {
        preferences.remove(
            listOf(
                PrismPreferences.KEY_BEST_SCORE,
                PrismPreferences.KEY_BEST_STREAK,
                PrismPreferences.KEY_RUNS,
                PrismPreferences.KEY_DROPS
            )
        )
    }
}
