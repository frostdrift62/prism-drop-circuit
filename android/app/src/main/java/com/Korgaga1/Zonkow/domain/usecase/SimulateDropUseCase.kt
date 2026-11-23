package com.Korgaga1.Zonkow.domain.usecase

import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.domain.model.BoardLayout
import com.Korgaga1.Zonkow.domain.model.DropOutcome
import com.Korgaga1.Zonkow.domain.model.GateState
import kotlin.math.abs

class SimulateDropUseCase {

    operator fun invoke(
        layout: BoardLayout,
        gates: List<GateState>,
        startLane: Int
    ): DropOutcome {
        val lastLane = GameConfig.COLS - 1
        var lane = startLane.coerceIn(0, lastLane)
        val path = ArrayList<Int>(GameConfig.BOARD_ROWS + 1)
        path.add(lane)
        for (row in 0 until GameConfig.BOARD_ROWS) {
            val gateSlot = GameConfig.GATE_ROWS.indexOf(row)
            val gate = if (gateSlot >= 0) gates.getOrNull(gateSlot) else null
            lane = if (gate != null && abs(gate.lane - lane) <= 1) {
                if (gate.facingRight) lane + 1 else lane - 1
            } else {
                lane + deflection(layout, row, lane)
            }
            lane = lane.coerceIn(0, lastLane)
            path.add(lane)
        }
        return DropOutcome(
            path = path,
            finalLane = lane,
            hitTarget = lane == layout.targetLane,
            hitVoid = lane == layout.voidLane,
            laneValue = GameConfig.LANE_VALUES[lane]
        )
    }

    private fun deflection(layout: BoardLayout, row: Int, lane: Int): Int {
        val rowTable = layout.deflections.getOrNull(row) ?: return 1
        return rowTable.getOrNull(lane) ?: 1
    }
}
