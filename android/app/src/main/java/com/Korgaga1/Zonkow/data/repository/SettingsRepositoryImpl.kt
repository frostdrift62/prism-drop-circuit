package com.Korgaga1.Zonkow.data.repository

import com.Korgaga1.Zonkow.data.local.PrismPreferences
import com.Korgaga1.Zonkow.domain.model.GameSettings
import com.Korgaga1.Zonkow.domain.model.Tier
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: PrismPreferences) : SettingsRepository {

    override fun settings(): GameSettings = GameSettings(
        tier = Tier.fromId(preferences.readString(PrismPreferences.KEY_TIER, Tier.Standard.id)),
        highContrastMarks = preferences.readBoolean(PrismPreferences.KEY_HIGH_CONTRAST, false),
        reducedMotion = preferences.readBoolean(PrismPreferences.KEY_REDUCED_MOTION, false)
    )

    override fun setTier(tier: Tier) {
        preferences.writeString(PrismPreferences.KEY_TIER, tier.id)
    }

    override fun setHighContrastMarks(enabled: Boolean) {
        preferences.writeBoolean(PrismPreferences.KEY_HIGH_CONTRAST, enabled)
    }

    override fun setReducedMotion(enabled: Boolean) {
        preferences.writeBoolean(PrismPreferences.KEY_REDUCED_MOTION, enabled)
    }
}
