package com.Korgaga1.Zonkow.presentation.settings

import com.Korgaga1.Zonkow.domain.model.Tier

data class SettingsUiState(
    val tier: Tier = Tier.Standard,
    val highContrastMarks: Boolean = false,
    val reducedMotion: Boolean = false,
    val hasHistory: Boolean = false,
    val confirmingReset: Boolean = false
)
