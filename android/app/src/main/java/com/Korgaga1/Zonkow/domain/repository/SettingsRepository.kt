package com.Korgaga1.Zonkow.domain.repository

import com.Korgaga1.Zonkow.domain.model.GameSettings
import com.Korgaga1.Zonkow.domain.model.Tier

interface SettingsRepository {
    fun settings(): GameSettings
    fun setTier(tier: Tier)
    fun setHighContrastMarks(enabled: Boolean)
    fun setReducedMotion(enabled: Boolean)
}
