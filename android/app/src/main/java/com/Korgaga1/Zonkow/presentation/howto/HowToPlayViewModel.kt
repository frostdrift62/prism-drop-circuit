package com.Korgaga1.Zonkow.presentation.howto

import androidx.lifecycle.ViewModel
import com.Korgaga1.Zonkow.data.sample.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HowToPlayViewModel : ViewModel() {

    private val _state = MutableStateFlow(HowToPlayUiState(steps = SampleData.howToSteps))
    val state: StateFlow<HowToPlayUiState> = _state.asStateFlow()
}
