package com.Korgaga1.Zonkow.core.navigation

import com.Korgaga1.Zonkow.domain.model.RunResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Game : Screen
    data object HowToPlay : Screen
    data object Settings : Screen
    data class GameOver(val result: RunResult) : Screen
}
