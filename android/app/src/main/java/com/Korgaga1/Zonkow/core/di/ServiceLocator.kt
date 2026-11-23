package com.Korgaga1.Zonkow.core.di

import android.content.Context
import com.Korgaga1.Zonkow.data.local.PrismPreferences
import com.Korgaga1.Zonkow.data.repository.ProgressRepositoryImpl
import com.Korgaga1.Zonkow.data.repository.SettingsRepositoryImpl
import com.Korgaga1.Zonkow.domain.repository.ProgressRepository
import com.Korgaga1.Zonkow.domain.repository.SettingsRepository

object ServiceLocator {

    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null
    private var viewModelFactory: AppViewModelFactory? = null

    fun init(context: Context) {
        if (viewModelFactory != null) {
            return
        }
        val preferences = PrismPreferences(context.applicationContext)
        val progressRepository = ProgressRepositoryImpl(preferences)
        val settingsRepository = SettingsRepositoryImpl(preferences)
        progress = progressRepository
        settings = settingsRepository
        viewModelFactory = AppViewModelFactory(progressRepository, settingsRepository)
    }

    fun factory(): AppViewModelFactory = checkNotNull(viewModelFactory)

    fun progress(): ProgressRepository = checkNotNull(progress)

    fun settings(): SettingsRepository = checkNotNull(settings)
}
