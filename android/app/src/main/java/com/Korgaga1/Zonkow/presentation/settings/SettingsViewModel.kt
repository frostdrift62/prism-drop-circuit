package com.Korgaga1.Zonkow.presentation.settings

import androidx.lifecycle.ViewModel
import com.Korgaga1.Zonkow.domain.model.Tier
import com.Korgaga1.Zonkow.domain.repository.ProgressRepository
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val settings = settingsRepository.settings()
        val stats = progressRepository.stats()
        _state.value = SettingsUiState(
            tier = settings.tier,
            highContrastMarks = settings.highContrastMarks,
            reducedMotion = settings.reducedMotion,
            hasHistory = stats.hasHistory,
            confirmingReset = false
        )
    }

    fun onTierSelected(tier: Tier) {
        settingsRepository.setTier(tier)
        _state.update { it.copy(tier = tier) }
    }

    fun onHighContrastChanged(enabled: Boolean) {
        settingsRepository.setHighContrastMarks(enabled)
        _state.update { it.copy(highContrastMarks = enabled) }
    }

    fun onReducedMotionChanged(enabled: Boolean) {
        settingsRepository.setReducedMotion(enabled)
        _state.update { it.copy(reducedMotion = enabled) }
    }

    fun onResetRequested() {
        _state.update { it.copy(confirmingReset = true) }
    }

    fun onResetCancelled() {
        _state.update { it.copy(confirmingReset = false) }
    }

    fun onResetConfirmed() {
        progressRepository.resetProgress()
        _state.update { it.copy(confirmingReset = false, hasHistory = false) }
    }
}
