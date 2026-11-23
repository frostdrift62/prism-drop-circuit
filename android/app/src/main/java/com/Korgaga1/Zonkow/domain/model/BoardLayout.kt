package com.Korgaga1.Zonkow.domain.model

data class BoardLayout(
    val gates: List<GateState>,
    val targetLane: Int,
    val voidLane: Int,
    val deflections: List<List<Int>>,
    val seed: Int,
    val fallback: Boolean
)
