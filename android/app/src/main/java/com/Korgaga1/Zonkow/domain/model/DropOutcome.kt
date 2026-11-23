package com.Korgaga1.Zonkow.domain.model

data class DropOutcome(
    val path: List<Int>,
    val finalLane: Int,
    val hitTarget: Boolean,
    val hitVoid: Boolean,
    val laneValue: Int
)
