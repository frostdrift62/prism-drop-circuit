package com.Korgaga1.Zonkow.domain.usecase

import com.Korgaga1.Zonkow.core.config.GameConfig
import com.Korgaga1.Zonkow.domain.model.BoardLayout
import com.Korgaga1.Zonkow.domain.model.GateState
import kotlin.random.Random

class GenerateBoardUseCase(private val simulateDrop: SimulateDropUseCase) {

    operator fun invoke(runIndex: Int, dropIndex: Int, moveBudget: Int): BoardLayout {
        val seed = (runIndex * 7919) xor ((dropIndex + 1) * 104729)
        var attempt = 0
        while (attempt < GameConfig.GENERATOR_ATTEMPTS) {
            val candidate = build(seed + attempt * 31, Random(seed + attempt * 31))
            if (isSolvable(candidate, moveBudget)) {
                return candidate
            }
            attempt++
        }
        return fallback(seed)
    }

    private fun build(seed: Int, random: Random): BoardLayout {
        val lastLane = GameConfig.COLS - 1
        val gates = GameConfig.GATE_ROWS.indices.map { slot ->
            GateState(
                index = slot,
                lane = random.nextInt(GameConfig.COLS),
                facingRight = random.nextBoolean()
            )
        }
        val targetLane = random.nextInt(GameConfig.COLS)
        var voidLane = random.nextInt(GameConfig.COLS)
        if (voidLane == targetLane) {
            voidLane = if (targetLane == lastLane) targetLane - 1 else targetLane + 1
        }
        val deflections = (0 until GameConfig.BOARD_ROWS).map {
            (0 until GameConfig.COLS).map { if (random.nextBoolean()) 1 else -1 }
        }
        return BoardLayout(
            gates = gates,
            targetLane = targetLane,
            voidLane = voidLane,
            deflections = deflections,
            seed = seed,
            fallback = false
        )
    }

    private fun fallback(seed: Int): BoardLayout {
        val gates = listOf(
            GateState(0, 2, true),
            GateState(1, 4, false),
            GateState(2, 6, true)
        )
        val deflections = (0 until GameConfig.BOARD_ROWS).map { row ->
            (0 until GameConfig.COLS).map { lane -> if ((row + lane) % 2 == 0) 1 else -1 }
        }
        return BoardLayout(
            gates = gates,
            targetLane = 4,
            voidLane = 1,
            deflections = deflections,
            seed = seed,
            fallback = true
        )
    }

    private fun isSolvable(layout: BoardLayout, moveBudget: Int): Boolean {
        var frontier = listOf(layout.gates)
        val seen = HashSet<String>()
        seen.add(key(layout.gates))
        var depth = 0
        while (true) {
            for (configuration in frontier) {
                val outcome = simulateDrop(layout, configuration, GameConfig.START_LANE)
                if (outcome.finalLane == layout.targetLane) {
                    return true
                }
            }
            if (depth >= moveBudget) {
                return false
            }
            val next = ArrayList<List<GateState>>()
            for (configuration in frontier) {
                for (slot in configuration.indices) {
                    val gate = configuration[slot]
                    val flipped = configuration.toMutableList()
                    flipped[slot] = gate.copy(facingRight = !gate.facingRight)
                    if (seen.add(key(flipped))) {
                        next.add(flipped)
                    }
                    for (lane in 0 until GameConfig.COLS) {
                        if (lane == gate.lane) continue
                        val moved = configuration.toMutableList()
                        moved[slot] = gate.copy(lane = lane)
                        if (seen.add(key(moved))) {
                            next.add(moved)
                        }
                    }
                }
            }
            if (next.isEmpty()) {
                return false
            }
            frontier = next
            depth++
        }
    }

    private fun key(gates: List<GateState>): String =
        gates.joinToString(separator = "-") { gate ->
            gate.lane.toString() + if (gate.facingRight) "r" else "l"
        }
}
