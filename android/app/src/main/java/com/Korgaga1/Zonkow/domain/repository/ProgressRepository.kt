package com.Korgaga1.Zonkow.domain.repository

import com.Korgaga1.Zonkow.domain.model.RunStats

interface ProgressRepository {
    fun stats(): RunStats
    fun nextRunIndex(): Int
    fun recordRun(score: Int, drops: Int, streak: Int): Boolean
    fun resetProgress()
}
