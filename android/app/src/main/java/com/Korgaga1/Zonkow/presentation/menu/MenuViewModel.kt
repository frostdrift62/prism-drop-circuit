package com.Korgaga1.Zonkow.presentation.menu

import androidx.lifecycle.ViewModel
import com.Korgaga1.Zonkow.domain.repository.ProgressRepository
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val stats = progressRepository.stats()
        val settings = settingsRepository.settings()
        _state.value = MenuUiState(
            bestScore = stats.bestScore,
            bestStreak = stats.bestStreak,
            runsCompleted = stats.runsCompleted,
            lifetimeDrops = stats.lifetimeDrops,
            tier = settings.tier,
            reducedMotion = settings.reducedMotion,
            hasHistory = stats.hasHistory
        )
    }
}
