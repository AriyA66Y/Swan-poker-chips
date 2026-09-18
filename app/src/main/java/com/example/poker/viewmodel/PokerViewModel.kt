package com.example.poker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poker.logic.PotCalculator
import com.example.poker.model.ActionType
import com.example.poker.model.BuyInRecord
import com.example.poker.model.ChipAdjustment
import com.example.poker.model.GameSettings
import com.example.poker.model.HandResult
import com.example.poker.model.Player
import com.example.poker.model.PlayerAction
import com.example.poker.model.PlayerHandStatus
import com.example.poker.model.Pot
import com.example.poker.model.Street
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.min

data class PokerUiState(
    val players: List<Player> = emptyList(),
    val dealerIndex: Int = 0,
    val currentStreet: Street = Street.ENDED,
    val currentTurnPlayerId: String? = null,
    val currentHighestBet: Long = 0L,
    val minRaise: Long = 20L,
    val pots: List<Pot> = emptyList(),
    val handNumber: Int = 0,
    val actionLogs: List<PlayerAction> = emptyList(),
    val handHistory: List<HandResult> = emptyList(),
    val buyInRecords: List<BuyInRecord> = emptyList(),
    val chipAdjustments: List<ChipAdjustment> = emptyList(),
    val settings: GameSettings = GameSettings(),
    val isShowdownModalOpen: Boolean = false,
    val isAutoHandOver: Boolean = false,
    val lastHandSummary: String? = null
)

class PokerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PokerUiState())
    val uiState: StateFlow<PokerUiState> = _uiState.asStateFlow()

    init {
        // Initialize with default standard players to make first launch instantly playable and exciting
        val defaultColors = listOf(
            0xFF1E88E5L, // Blue
            0xFFE53935L, // Red
            0xFF43A047L, // Green
            0xFF8E24AAL, // Purple
            0xFFFB8C00L, // Orange
            0xFF00ACC1L  // Teal
        )
        val initialPlayers = listOf(
            Player(name = "بازیکن ۱ (شما)", seatIndex = 0, chips = 1000L, totalBuyIn = 1000L, colorHex = defaultColors[0]),
            Player(name = "بازیکن ۲", seatIndex = 1, chips = 1000L, totalBuyIn = 1000L, colorHex = defaultColors[1]),
            Player(name = "بازیکن ۳", seatIndex = 2, chips = 1000L, totalBuyIn = 1000L, colorHex = defaultColors[2]),
            Player(name = "بازیکن ۴", seatIndex = 3, chips = 1000L, totalBuyIn = 1000L, colorHex = defaultColors[3])
        )
        val buyIns = initialPlayers.map {
            BuyInRecord(playerId = it.id, playerName = it.name, amount = it.totalBuyIn, isInitial = true)
        }
        _uiState.update { it.copy(players = initialPlayers, buyInRecords = buyIns) }
    }

    // -------------------------------------------------------------
    // PLAYER & CHIP MANAGEMENT
    // -------------------------------------------------------------

    fun addPlayer(name: String, buyIn: Long, colorHex: Long) {
        val currentPlayers = _uiState.value.players
        val nextSeat = if (currentPlayers.isEmpty()) 0 else (currentPlayers.maxOf { it.seatIndex } + 1)
        val newPlayer = Player(
            name = name.trim().ifEmpty { "بازیکن ${nextSeat + 1}" },
            seatIndex = nextSeat,
            chips = buyIn,
            totalBuyIn = buyIn,
            colorHex = colorHex,
            status = if (_uiState.value.currentStreet == Street.ENDED) PlayerHandStatus.ACTIVE else PlayerHandStatus.SITTING_OUT
        )
        val record = BuyInRecord(
            playerId = newPlayer.id,
            playerName = newPlayer.name,
            amount = buyIn,
            isInitial = true
        )
        _uiState.update { state ->
            state.copy(
                players = state.players + newPlayer,
                buyInRecords = state.buyInRecords + record
            )
        }
    }

    fun removePlayer(playerId: String) {
        _uiState.update { state ->
            val updated = state.players.filterNot { it.id == playerId }
            val newDealerIndex = if (state.dealerIndex >= updated.size && updated.isNotEmpty()) 0 else state.dealerIndex
            state.copy(players = updated, dealerIndex = newDealerIndex)
        }
    }

    fun rebuy(playerId: String, amount: Long) {
        if (amount <= 0) return
        _uiState.update { state ->
            var playerName = ""
            val updatedPlayers = state.players.map { player ->
                if (player.id == playerId) {
                    playerName = player.name
                    player.copy(
                        chips = player.chips + amount,
                        totalBuyIn = player.totalBuyIn + amount,
                        status = if (player.status == PlayerHandStatus.SITTING_OUT && state.currentStreet == Street.ENDED)
                            PlayerHandStatus.ACTIVE else player.status
                    )
                } else player
            }
            val record = BuyInRecord(
                playerId = playerId,
                playerName = playerName,
                amount = amount,
                isInitial = false
            )
            state.copy(players = updatedPlayers, buyInRecords = state.buyInRecords + record)
        }
    }

    fun adjustChipsManually(playerId: String, delta: Long, reason: String) {
        if (delta == 0L) return
        _uiState.update { state ->
            var playerName = ""
            val updatedPlayers = state.players.map { player ->
                if (player.id == playerId) {
                    playerName = player.name
                    val newChips = max(0L, player.chips + delta)
                    player.copy(chips = newChips)
                } else player
            }
            val adjustment = ChipAdjustment(
                playerId = playerId,
                playerName = playerName,
                delta = delta,
                reason = reason.ifEmpty { "تنظیم دستی توسط کاربر" }
            )
            state.copy(players = updatedPlayers, chipAdjustments = state.chipAdjustments + adjustment)
        }
    }

    fun updateSettings(newSettings: GameSettings) {
        _uiState.update { it.copy(settings = newSettings) }
    }

    // -------------------------------------------------------------
    // HAND MANAGEMENT
    // -------------------------------------------------------------

    fun startNewHand() {
        val state = _uiState.value
        val eligiblePlayers = state.players.filter { it.chips > 0 }
        if (eligiblePlayers.size < 2) return

        // Advance dealer button to next eligible player with chips
        val activeCount = state.players.size
        val nextDealer = (state.dealerIndex + 1) % activeCount

        // Reset all player hand state
        val resetPlayers = state.players.map { player ->
            if (player.chips > 0) {
                player.copy(
                    currentStreetBet = 0L,
                    totalHandContributed = 0L,
                    status = PlayerHandStatus.ACTIVE,
                    hasActedThisRound = false
                )
            } else {
                player.copy(
                    currentStreetBet = 0L,
                    totalHandContributed = 0L,
                    status = PlayerHandStatus.SITTING_OUT,
                    hasActedThisRound = true
                )
            }
        }

        val handNumber = state.handNumber + 1
        val actions = mutableListOf<PlayerAction>()

        // Find Small Blind and Big Blind positions
        val eligibleIndices = resetPlayers.indices.filter { resetPlayers[it].chips > 0 }
        if (eligibleIndices.size < 2) return

        val dealerPosInEligible = eligibleIndices.indexOf(nextDealer).let { if (it == -1) 0 else it }

        // In heads up (2 players), dealer is SB, other is BB
        val (sbIndex, bbIndex) = if (eligibleIndices.size == 2) {
            val sb = eligibleIndices[dealerPosInEligible]
            val bb = eligibleIndices[(dealerPosInEligible + 1) % eligibleIndices.size]
            Pair(sb, bb)
        } else {
            val sb = eligibleIndices[(dealerPosInEligible + 1) % eligibleIndices.size]
            val bb = eligibleIndices[(dealerPosInEligible + 2) % eligibleIndices.size]
            Pair(sb, bb)
        }

        // Post Blinds
        val sbAmount = min(state.settings.smallBlind, resetPlayers[sbIndex].chips)
        val bbAmount = min(state.settings.bigBlind, resetPlayers[bbIndex].chips)

        val updatedPlayersAfterBlinds = resetPlayers.toMutableList()

        // Deduct SB
        val sbPlayer = updatedPlayersAfterBlinds[sbIndex]
        updatedPlayersAfterBlinds[sbIndex] = sbPlayer.copy(
            chips = sbPlayer.chips - sbAmount,
            currentStreetBet = sbAmount,
            totalHandContributed = sbAmount,
            status = if (sbPlayer.chips - sbAmount == 0L) PlayerHandStatus.ALL_IN else PlayerHandStatus.ACTIVE
        )
        actions.add(
            PlayerAction(
                playerId = sbPlayer.id,
                playerName = sbPlayer.name,
                actionType = ActionType.POST_SB,
                amount = sbAmount,
                street = Street.PREFLOP
            )
        )

        // Deduct BB
        val bbPlayer = updatedPlayersAfterBlinds[bbIndex]
        updatedPlayersAfterBlinds[bbIndex] = bbPlayer.copy(
            chips = bbPlayer.chips - bbAmount,
            currentStreetBet = bbAmount,
            totalHandContributed = bbAmount,
            status = if (bbPlayer.chips - bbAmount == 0L) PlayerHandStatus.ALL_IN else PlayerHandStatus.ACTIVE
        )
        actions.add(
            PlayerAction(
                playerId = bbPlayer.id,
                playerName = bbPlayer.name,
                actionType = ActionType.POST_BB,
                amount = bbAmount,
                street = Street.PREFLOP
            )
        )

        // Calculate pots so far
        val (initialPots, _) = PotCalculator.calculatePots(updatedPlayersAfterBlinds)

        // First to act preflop is player after BB (UTG)
        val firstToActIndex = if (eligibleIndices.size == 2) {
            sbIndex // Heads-up preflop: SB acts first
        } else {
            eligibleIndices[(dealerPosInEligible + 3) % eligibleIndices.size]
        }

        val firstToActPlayerId = updatedPlayersAfterBlinds[firstToActIndex].id

        _uiState.update {
            it.copy(
                players = updatedPlayersAfterBlinds,
                dealerIndex = nextDealer,
                currentStreet = Street.PREFLOP,
                currentTurnPlayerId = firstToActPlayerId,
                currentHighestBet = state.settings.bigBlind,
                minRaise = state.settings.bigBlind,
                pots = initialPots,
                handNumber = handNumber,
                actionLogs = actions,
                isShowdownModalOpen = false,
                isAutoHandOver = false,
                lastHandSummary = null
            )
        }
    }

    // -------------------------------------------------------------
    // TURN ACTIONS
    // -------------------------------------------------------------

    fun onFold() {
        val state = _uiState.value
        val currentPlayer = getCurrentPlayer() ?: return

        val updatedPlayers = state.players.map {
            if (it.id == currentPlayer.id) it.copy(status = PlayerHandStatus.FOLDED, hasActedThisRound = true) else it
        }

        val action = PlayerAction(
            playerId = currentPlayer.id,
            playerName = currentPlayer.name,
            actionType = ActionType.FOLD,
            amount = 0L,
            street = state.currentStreet
        )

        // Check if only one non-folded player remains
        val nonFolded = updatedPlayers.filter {
            it.status != PlayerHandStatus.FOLDED && it.status != PlayerHandStatus.SITTING_OUT
        }

        if (nonFolded.size == 1) {
            // Instant winner! That player takes all pots!
            val singleWinner = nonFolded.first()
            val (calculatedPots, refunds) = PotCalculator.calculatePots(updatedPlayers)

            // Refund any uncalled excess bets
            val finalPlayers = updatedPlayers.map { p ->
                val refund = refunds[p.id] ?: 0L
                if (refund > 0) p.copy(chips = p.chips + refund) else p
            }.toMutableList()

            // Award all pots to single winner
            val totalWon = calculatedPots.sumOf { it.amount }
            val winnerIndex = finalPlayers.indexOfFirst { it.id == singleWinner.id }
            if (winnerIndex != -1) {
                finalPlayers[winnerIndex] = finalPlayers[winnerIndex].copy(
                    chips = finalPlayers[winnerIndex].chips + totalWon
                )
            }

            // Record hand result
            val deltas = mutableMapOf<String, Long>()
            for (p in finalPlayers) {
                val orig = state.players.firstOrNull { it.id == p.id }
                if (orig != null) {
                    deltas[p.id] = p.chips - orig.chips
                }
            }

            val summary = "${singleWinner.name} به دلیل فولد بقیه بازیکنان برنده شد (${totalWon} چیپ)"
            val handResult = HandResult(
                handNumber = state.handNumber,
                totalPot = totalWon,
                pots = calculatedPots.map { it.copy(winners = listOf(singleWinner.id)) },
                playerChipDeltas = deltas,
                winnersSummary = summary,
                actionLogs = state.actionLogs + action
            )

            _uiState.update {
                it.copy(
                    players = finalPlayers,
                    currentStreet = Street.ENDED,
                    currentTurnPlayerId = null,
                    actionLogs = it.actionLogs + action,
                    handHistory = it.handHistory + handResult,
                    pots = calculatedPots,
                    isAutoHandOver = true,
                    lastHandSummary = summary
                )
            }
            return
        }

        val (updatedPots, _) = PotCalculator.calculatePots(updatedPlayers)
        _uiState.update {
            it.copy(
                players = updatedPlayers,
                actionLogs = it.actionLogs + action,
                pots = updatedPots
            )
        }

        advanceTurn(updatedPlayers)
    }

    fun onCheck() {
        val state = _uiState.value
        val currentPlayer = getCurrentPlayer() ?: return

        // Check is only valid if player's currentStreetBet matches currentHighestBet
        if (currentPlayer.currentStreetBet < state.currentHighestBet) {
            // Cannot check, must call or fold
            return
        }

        val updatedPlayers = state.players.map {
            if (it.id == currentPlayer.id) it.copy(hasActedThisRound = true) else it
        }

        val action = PlayerAction(
            playerId = currentPlayer.id,
            playerName = currentPlayer.name,
            actionType = ActionType.CHECK,
            amount = 0L,
            street = state.currentStreet
        )

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                actionLogs = it.actionLogs + action
            )
        }

        advanceTurn(updatedPlayers)
    }

    fun onCall() {
        val state = _uiState.value
        val currentPlayer = getCurrentPlayer() ?: return

        val callDiff = state.currentHighestBet - currentPlayer.currentStreetBet
        if (callDiff <= 0) {
            onCheck()
            return
        }

        val amountToCall = min(callDiff, currentPlayer.chips)
        val newChips = currentPlayer.chips - amountToCall
        val isAllIn = (newChips == 0L)

        val updatedPlayers = state.players.map {
            if (it.id == currentPlayer.id) {
                it.copy(
                    chips = newChips,
                    currentStreetBet = it.currentStreetBet + amountToCall,
                    totalHandContributed = it.totalHandContributed + amountToCall,
                    status = if (isAllIn) PlayerHandStatus.ALL_IN else PlayerHandStatus.ACTIVE,
                    hasActedThisRound = true
                )
            } else it
        }

        val action = PlayerAction(
            playerId = currentPlayer.id,
            playerName = currentPlayer.name,
            actionType = if (isAllIn) ActionType.ALL_IN else ActionType.CALL,
            amount = amountToCall,
            street = state.currentStreet
        )

        val (updatedPots, _) = PotCalculator.calculatePots(updatedPlayers)

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                actionLogs = it.actionLogs + action,
                pots = updatedPots
            )
        }

        advanceTurn(updatedPlayers)
    }

    fun onBetOrRaise(targetTotalStreetBet: Long) {
        val state = _uiState.value
        val currentPlayer = getCurrentPlayer() ?: return

        val additionNeeded = targetTotalStreetBet - currentPlayer.currentStreetBet
        val actualAmount = min(additionNeeded, currentPlayer.chips)
        val finalStreetBet = currentPlayer.currentStreetBet + actualAmount
        val newChips = currentPlayer.chips - actualAmount
        val isAllIn = (newChips == 0L)

        val isRaise = finalStreetBet > state.currentHighestBet
        val raiseDiff = finalStreetBet - state.currentHighestBet
        val newMinRaise = if (isRaise && raiseDiff >= state.minRaise) raiseDiff else state.minRaise
        val newHighestBet = max(state.currentHighestBet, finalStreetBet)

        val updatedPlayers = state.players.map { p ->
            if (p.id == currentPlayer.id) {
                p.copy(
                    chips = newChips,
                    currentStreetBet = finalStreetBet,
                    totalHandContributed = p.totalHandContributed + actualAmount,
                    status = if (isAllIn) PlayerHandStatus.ALL_IN else PlayerHandStatus.ACTIVE,
                    hasActedThisRound = true
                )
            } else {
                // If a genuine raise happened, other active players must respond (unless already all-in)
                if (isRaise && p.status == PlayerHandStatus.ACTIVE) {
                    p.copy(hasActedThisRound = false)
                } else p
            }
        }

        val action = PlayerAction(
            playerId = currentPlayer.id,
            playerName = currentPlayer.name,
            actionType = if (isAllIn) ActionType.ALL_IN else if (state.currentHighestBet == 0L) ActionType.BET else ActionType.RAISE,
            amount = actualAmount,
            street = state.currentStreet
        )

        val (updatedPots, _) = PotCalculator.calculatePots(updatedPlayers)

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                currentHighestBet = newHighestBet,
                minRaise = newMinRaise,
                actionLogs = it.actionLogs + action,
                pots = updatedPots
            )
        }

        advanceTurn(updatedPlayers)
    }

    fun onAllIn() {
        val currentPlayer = getCurrentPlayer() ?: return
        val totalBetTarget = currentPlayer.currentStreetBet + currentPlayer.chips
        onBetOrRaise(totalBetTarget)
    }

    // -------------------------------------------------------------
    // TURN ADVANCEMENT & STREET PROGRESSION
    // -------------------------------------------------------------

    private fun advanceTurn(players: List<Player>) {
        val state = _uiState.value
        val activePlayers = players.filter {
            it.status == PlayerHandStatus.ACTIVE
        }

        // Check if round is complete:
        // Round is complete when every ACTIVE player has acted (hasActedThisRound == true)
        // AND all active players have contributed the same currentStreetBet (or are all-in)
        val allActed = activePlayers.all { it.hasActedThisRound }
        val betsEqualized = activePlayers.map { it.currentStreetBet }.distinct().size <= 1

        val allInOrFoldedCount = players.count {
            it.status == PlayerHandStatus.ALL_IN || it.status == PlayerHandStatus.FOLDED || it.status == PlayerHandStatus.SITTING_OUT
        }
        val remainingActiveToBet = players.count { it.status == PlayerHandStatus.ACTIVE }

        if (allActed && betsEqualized) {
            // Move to next street
            progressStreet(players)
            return
        }

        // If only 1 or 0 players can act because everyone else is all-in or folded,
        // and bets are equalized, auto progress directly!
        if (remainingActiveToBet <= 1 && betsEqualized) {
            progressStreet(players)
            return
        }

        // Find next player whose turn it is
        val currentIdx = players.indexOfFirst { it.id == state.currentTurnPlayerId }
        var nextIdx = (currentIdx + 1) % players.size
        var attempts = 0
        var foundNext = false

        while (attempts < players.size) {
            val candidate = players[nextIdx]
            if (candidate.status == PlayerHandStatus.ACTIVE && (!candidate.hasActedThisRound || candidate.currentStreetBet < state.currentHighestBet)) {
                foundNext = true
                break
            }
            nextIdx = (nextIdx + 1) % players.size
            attempts++
        }

        if (foundNext) {
            _uiState.update { it.copy(currentTurnPlayerId = players[nextIdx].id) }
        } else {
            // If no next player needed to act, street complete
            progressStreet(players)
        }
    }

    private fun progressStreet(players: List<Player>) {
        val state = _uiState.value

        // Check if showdown is needed
        val nextStreet = when (state.currentStreet) {
            Street.PREFLOP -> Street.FLOP
            Street.FLOP -> Street.TURN
            Street.TURN -> Street.RIVER
            Street.RIVER -> Street.SHOWDOWN
            else -> Street.SHOWDOWN
        }

        // Reset street bets and flags for the new street
        val resetForNewStreet = players.map { p ->
            p.copy(
                currentStreetBet = 0L,
                hasActedThisRound = (p.status != PlayerHandStatus.ACTIVE)
            )
        }

        val (updatedPots, refunds) = PotCalculator.calculatePots(resetForNewStreet)

        if (nextStreet == Street.SHOWDOWN) {
            // Trigger Showdown Modal for assigning pot winners
            _uiState.update {
                it.copy(
                    players = resetForNewStreet,
                    currentStreet = Street.SHOWDOWN,
                    currentTurnPlayerId = null,
                    currentHighestBet = 0L,
                    minRaise = state.settings.bigBlind,
                    pots = updatedPots,
                    isShowdownModalOpen = true
                )
            }
            return
        }

        // Check if multiple active players can still bet
        val activeBettors = resetForNewStreet.filter { it.status == PlayerHandStatus.ACTIVE }
        if (activeBettors.size <= 1) {
            // If at most 1 player has chips left to bet, fast-forward directly to Showdown
            _uiState.update {
                it.copy(
                    players = resetForNewStreet,
                    currentStreet = Street.SHOWDOWN,
                    currentTurnPlayerId = null,
                    currentHighestBet = 0L,
                    minRaise = state.settings.bigBlind,
                    pots = updatedPots,
                    isShowdownModalOpen = true
                )
            }
            return
        }

        // First player to act on Flop/Turn/River is first active player after dealer button
        var firstActorIdx = (state.dealerIndex + 1) % resetForNewStreet.size
        var loopCount = 0
        while (resetForNewStreet[firstActorIdx].status != PlayerHandStatus.ACTIVE && loopCount < resetForNewStreet.size) {
            firstActorIdx = (firstActorIdx + 1) % resetForNewStreet.size
            loopCount++
        }

        val firstPlayerId = resetForNewStreet[firstActorIdx].id

        _uiState.update {
            it.copy(
                players = resetForNewStreet,
                currentStreet = nextStreet,
                currentTurnPlayerId = firstPlayerId,
                currentHighestBet = 0L,
                minRaise = state.settings.bigBlind,
                pots = updatedPots
            )
        }
    }

    // -------------------------------------------------------------
    // SHOWDOWN & POT RESOLUTION
    // -------------------------------------------------------------

    fun awardPots(potWinnersMap: Map<String, List<String>>) {
        val state = _uiState.value
        val (currentPots, refunds) = PotCalculator.calculatePots(state.players)

        val updatedPlayers = state.players.map { p ->
            val refund = refunds[p.id] ?: 0L
            if (refund > 0) p.copy(chips = p.chips + refund) else p
        }.toMutableList()

        val summaries = mutableListOf<String>()

        val awardedPots = currentPots.map { pot ->
            val winners = potWinnersMap[pot.id] ?: emptyList()
            if (winners.isNotEmpty()) {
                val payouts = PotCalculator.splitPot(pot.amount, winners)
                payouts.forEach { (winnerId, amount) ->
                    val idx = updatedPlayers.indexOfFirst { it.id == winnerId }
                    if (idx != -1) {
                        val winner = updatedPlayers[idx]
                        updatedPlayers[idx] = winner.copy(chips = winner.chips + amount)
                    }
                }
                val winnerNames = winners.mapNotNull { wid -> updatedPlayers.firstOrNull { it.id == wid }?.name }
                summaries.add("${pot.name}: برنده ${winnerNames.joinToString(", ")} (${pot.amount} چیپ)")
            }
            pot.copy(winners = winners)
        }

        // Calculate deltas for this hand
        val deltas = mutableMapOf<String, Long>()
        for (p in updatedPlayers) {
            val initialChips = state.players.firstOrNull { it.id == p.id }?.chips ?: p.chips
            // Note: the player already had contributed deducted from their stack, so delta is final - initial
            deltas[p.id] = p.chips - initialChips
        }

        val fullSummary = summaries.joinToString(" | ")
        val handResult = HandResult(
            handNumber = state.handNumber,
            totalPot = currentPots.sumOf { it.amount },
            pots = awardedPots,
            playerChipDeltas = deltas,
            winnersSummary = fullSummary,
            actionLogs = state.actionLogs
        )

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                currentStreet = Street.ENDED,
                currentTurnPlayerId = null,
                pots = awardedPots,
                handHistory = it.handHistory + handResult,
                isShowdownModalOpen = false,
                lastHandSummary = fullSummary
            )
        }
    }

    fun dismissShowdownModal() {
        _uiState.update { it.copy(isShowdownModalOpen = false) }
    }

    private fun getCurrentPlayer(): Player? {
        val state = _uiState.value
        return state.players.firstOrNull { it.id == state.currentTurnPlayerId }
    }
}
