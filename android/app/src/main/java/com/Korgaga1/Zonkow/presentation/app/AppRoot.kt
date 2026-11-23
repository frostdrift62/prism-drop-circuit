package com.Korgaga1.Zonkow.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Korgaga1.Zonkow.core.di.ServiceLocator
import com.Korgaga1.Zonkow.core.navigation.Screen
import com.Korgaga1.Zonkow.core.theme.BaseVoid
import com.Korgaga1.Zonkow.presentation.game.GameScreen
import com.Korgaga1.Zonkow.presentation.gameover.GameOverScreen
import com.Korgaga1.Zonkow.presentation.howto.HowToPlayScreen
import com.Korgaga1.Zonkow.presentation.menu.MenuScreen
import com.Korgaga1.Zonkow.presentation.settings.SettingsScreen
import com.Korgaga1.Zonkow.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory())
    val state by appViewModel.state.collectAsStateWithLifecycle()
    val screen = state.screen

    BackHandler(enabled = screen !is Screen.Splash && screen !is Screen.Menu) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseVoid)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (state.reducedMotion) {
                    fadeIn(tween(120)) togetherWith fadeOut(tween(120))
                } else {
                    val enter = fadeIn(tween(260)) + slideInVertically { full -> full / 8 }
                    enter togetherWith fadeOut(tween(180))
                }
            }
        ) { target ->
            when (target) {
                Screen.Splash -> SplashScreen(onFinished = appViewModel::openMenu)

                Screen.Menu -> MenuScreen(
                    onPlay = { appViewModel.navigate(Screen.Game) },
                    onHowToPlay = { appViewModel.navigate(Screen.HowToPlay) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                Screen.Game -> GameScreen(
                    onExit = appViewModel::openMenu,
                    onFinished = appViewModel::showResult
                )

                Screen.HowToPlay -> HowToPlayScreen(onMenu = appViewModel::back)

                Screen.Settings -> SettingsScreen(onMenu = appViewModel::back)

                is Screen.GameOver -> GameOverScreen(
                    result = target.result,
                    onReplay = appViewModel::replay,
                    onMenu = appViewModel::openMenu
                )
            }
        }
    }
}
