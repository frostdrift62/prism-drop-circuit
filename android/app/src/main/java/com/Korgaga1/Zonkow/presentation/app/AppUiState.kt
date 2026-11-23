package com.Korgaga1.Zonkow.presentation.app

import com.Korgaga1.Zonkow.core.navigation.Screen

data class AppUiState(
    val screen: Screen = Screen.Splash,
    val reducedMotion: Boolean = false
)
