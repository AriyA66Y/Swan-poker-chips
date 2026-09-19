package com.example.poker.logic

import com.example.poker.model.Player
import com.example.poker.model.PlayerHandStatus
import com.example.poker.model.Pot
import kotlin.math.min

object PotCalculator {

    /**
     * Calculates the Main Pot and any Side Pots dynamically based on players' total contributions
     * and their status (active, all-in, folded).
     *
     * Returns a pair of:
     * - List of Pots (Main Pot, Side Pot 1, Side Pot 2, ...)
     * - Map of uncalled bet refunds (playerId -> refundAmount) if someone bet more than everyone else could call.
     */
    fun calculatePots(players: List<Player>): Pair<List<Pot>, Map<String, Long>> {
        val playersWithContrib = players.filter { it.totalHandContributed > 0 }
        if (playersWithContrib.isEmpty()) {
            return Pair(emptyList(), emptyMap())
        }

        // Distinct positive contribution thresholds sorted ascending
        val contributionLevels = playersWithContrib
            .map { it.totalHandContributed }
            .distinct()
            .sorted()

        var previousLevel = 0L
        val rawPots = mutableListOf<RawPotSlice>()
        val refunds = mutableMapOf<String, Long>()

        for (level in contributionLevels) {
            val sliceHeight = level - previousLevel
            if (sliceHeight <= 0) continue

            var sliceAmount = 0L
            val eligiblePlayers = mutableListOf<String>()

            for (p in playersWithContrib) {
                if (p.totalHandContributed > previousLevel) {
                    val contributionInSlice = min(p.totalHandContributed - previousLevel, sliceHeight)
                    sliceAmount += contributionInSlice
                }

                // A player is eligible to win this slice if they contributed at least up to this level
                // AND they have not folded or sat out
                if (p.totalHandContributed >= level &&
                    p.status != PlayerHandStatus.FOLDED &&
                    p.status != PlayerHandStatus.SITTING_OUT
                ) {
                    eligiblePlayers.add(p.id)
                }
            }

            if (sliceAmount > 0) {
                if (eligiblePlayers.isEmpty()) {
                    // Everyone who reached this level folded; pot slice rolls into previous pot or highest active player
                    if (rawPots.isNotEmpty()) {
                        val last = rawPots.removeAt(rawPots.size - 1)
                        rawPots.add(last.copy(amount = last.amount + sliceAmount))
                    }
                } else if (eligiblePlayers.size == 1) {
                    // Uncalled bet: only 1 non-folded player contributed at this level.
                    // This slice should be refunded directly to that single player!
                    val singlePlayerId = eligiblePlayers.first()
                    refunds[singlePlayerId] = (refunds[singlePlayerId] ?: 0L) + sliceAmount
                } else {
                    rawPots.add(RawPotSlice(amount = sliceAmount, eligiblePlayerIds = eligiblePlayers))
                }
            }

            previousLevel = level
        }

        // If no pots were created (e.g. only 1 player entered), handle edge case
        if (rawPots.isEmpty()) {
            val totalInPot = playersWithContrib.sumOf { it.totalHandContributed }
            val activeNonFolded = players.filter {
                it.status != PlayerHandStatus.FOLDED && it.status != PlayerHandStatus.SITTING_OUT
            }
            if (totalInPot > 0 && activeNonFolded.isNotEmpty()) {
                val singleWinner = activeNonFolded.first()
                return Pair(
                    listOf(
                        Pot(
                            id = "pot_0",
                            name = "پات اصلی (Main Pot)",
                            amount = totalInPot,
                            eligiblePlayerIds = listOf(singleWinner.id)
                        )
                    ),
                    emptyMap()
                )
            }
            return Pair(emptyList(), emptyMap())
        }

        // Merge adjacent slices that have the exact same set of eligible players
        val mergedPots = mutableListOf<RawPotSlice>()
        for (slice in rawPots) {
            if (mergedPots.isNotEmpty() && mergedPots.last().eligiblePlayerIds.toSet() == slice.eligiblePlayerIds.toSet()) {
                val last = mergedPots.removeAt(mergedPots.size - 1)
                mergedPots.add(last.copy(amount = last.amount + slice.amount))
            } else {
                mergedPots.add(slice)
            }
        }

        // Convert to named Pot objects (Main Pot, Side Pot 1, Side Pot 2, ...)
        val resultPots = mergedPots.mapIndexed { index, slice ->
            val name = if (index == 0) {
                "پات اصلی (Main Pot)"
            } else {
                "ساید پات $index (Side Pot $index)"
            }
            Pot(
                id = "pot_$index",
                name = name,
                amount = slice.amount,
                eligiblePlayerIds = slice.eligiblePlayerIds
            )
        }

        return Pair(resultPots, refunds)
    }

    /**
     * Splits a pot among multiple winners.
     * Returns a map of playerId -> awardedAmount.
     */
    fun splitPot(potAmount: Long, winnerIds: List<String>): Map<String, Long> {
        if (winnerIds.isEmpty() || potAmount <= 0) return emptyMap()

        val share = potAmount / winnerIds.size
        var remainder = potAmount % winnerIds.size

        val payouts = mutableMapOf<String, Long>()
        for (id in winnerIds) {
            val extra = if (remainder > 0) {
                remainder--
                1L
            } else 0L
            payouts[id] = share + extra
        }
        return payouts
    }

    private data class RawPotSlice(
        val amount: Long,
        val eligiblePlayerIds: List<String>
    )
}
