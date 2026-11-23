package com.Korgaga1.Zonkow.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Korgaga1.Zonkow.domain.repository.ProgressRepository
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository
import com.Korgaga1.Zonkow.domain.usecase.BuildPatternUseCase
import com.Korgaga1.Zonkow.domain.usecase.GenerateBoardUseCase
import com.Korgaga1.Zonkow.domain.usecase.ScoreDropUseCase
import com.Korgaga1.Zonkow.domain.usecase.SimulateDropUseCase
import com.Korgaga1.Zonkow.presentation.app.AppViewModel
import com.Korgaga1.Zonkow.presentation.game.GameViewModel
import com.Korgaga1.Zonkow.presentation.gameover.GameOverViewModel
import com.Korgaga1.Zonkow.presentation.howto.HowToPlayViewModel
import com.Korgaga1.Zonkow.presentation.menu.MenuViewModel
import com.Korgaga1.Zonkow.presentation.settings.SettingsViewModel
import com.Korgaga1.Zonkow.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    private val simulateDrop = SimulateDropUseCase()
    private val generateBoard = GenerateBoardUseCase(simulateDrop)
    private val buildPattern = BuildPatternUseCase()
    private val scoreDrop = ScoreDropUseCase()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(settingsRepository)

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(settingsRepository)

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(progressRepository, settingsRepository)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(
                    progressRepository = progressRepository,
                    settingsRepository = settingsRepository,
                    generateBoard = generateBoard,
                    simulateDrop = simulateDrop,
                    buildPattern = buildPattern,
                    scoreDrop = scoreDrop
                )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(progressRepository)

            modelClass.isAssignableFrom(HowToPlayViewModel::class.java) ->
                HowToPlayViewModel()

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settingsRepository, progressRepository)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
