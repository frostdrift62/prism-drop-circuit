package com.Korgaga1.Zonkow.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Korgaga1.Zonkow.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(private val progressRepository: ProgressRepository) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val stats = progressRepository.stats()
        _state.value = GameOverUiState(
            bestScore = stats.bestScore,
            runsCompleted = stats.runsCompleted
        )
    }
}
