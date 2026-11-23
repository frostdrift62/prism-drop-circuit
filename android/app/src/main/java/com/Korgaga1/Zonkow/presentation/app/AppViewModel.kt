package com.Korgaga1.Zonkow.presentation.app

import androidx.lifecycle.ViewModel
import com.Korgaga1.Zonkow.core.navigation.Screen
import com.Korgaga1.Zonkow.domain.model.RunResult
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    private val backStack = ArrayList<Screen>()

    fun openMenu() {
        backStack.clear()
        show(Screen.Menu)
    }

    fun navigate(target: Screen) {
        val current = _state.value.screen
        if (current != Screen.Splash) {
            backStack.add(current)
        }
        show(target)
    }

    fun showResult(result: RunResult) {
        backStack.clear()
        backStack.add(Screen.Menu)
        show(Screen.GameOver(result))
    }

    fun replay() {
        backStack.clear()
        backStack.add(Screen.Menu)
        show(Screen.Game)
    }

    fun back() {
        val previous = if (backStack.isEmpty()) null else backStack.removeAt(backStack.size - 1)
        if (previous == null) {
            openMenu()
        } else {
            show(previous)
        }
    }

    private fun show(target: Screen) {
        val reduced = settingsRepository.settings().reducedMotion
        _state.update { it.copy(screen = target, reducedMotion = reduced) }
    }
}
