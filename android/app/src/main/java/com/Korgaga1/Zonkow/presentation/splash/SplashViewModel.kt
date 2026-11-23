package com.Korgaga1.Zonkow.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(settingsRepository: SettingsRepository) : ViewModel() {

    private val _state = MutableStateFlow(
        SplashUiState(reducedMotion = settingsRepository.settings().reducedMotion)
    )
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            _state.update { it.copy(finished = true) }
        }
    }
}
