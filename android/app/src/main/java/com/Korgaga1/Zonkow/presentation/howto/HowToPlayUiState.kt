package com.Korgaga1.Zonkow.presentation.howto

import com.Korgaga1.Zonkow.domain.model.HowToStep

data class HowToPlayUiState(
    val steps: List<HowToStep> = emptyList()
)
