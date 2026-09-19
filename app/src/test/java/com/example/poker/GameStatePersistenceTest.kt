package com.example.poker

import com.example.poker.data.GameStateSerializer
import com.example.poker.model.BuyInRecord
import com.example.poker.model.GameSettings
import com.example.poker.model.HandResult
import com.example.poker.model.Player
import com.example.poker.model.PlayerHandStatus
import com.example.poker.model.Pot
import com.example.poker.model.Street
import com.example.poker.viewmodel.PokerUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GameStatePersistenceTest {

    @Test
    fun testSerializationAndDeserializationPreservesState() {
        val players = listOf(
            Player(id = "p1", name = "علی", seatIndex = 0, chips = 1500L, totalBuyIn = 1000L, status = PlayerHandStatus.ACTIVE),
            Player(id = "p2", name = "رضا", seatIndex = 1, chips = 500L, totalBuyIn = 1000L, status = PlayerHandStatus.FOLDED)
        )
        val pots = listOf(
            Pot(id = "pot_0", name = "پات اصلی", amount = 300L, eligiblePlayerIds = listOf("p1", "p2"))
        )
        val handHistory = listOf(
            HandResult(
                handNumber = 1,
                totalPot = 200L,
                pots = emptyList(),
                playerChipDeltas = mapOf("p1" to 100L, "p2" to -100L),
                winnersSummary = "پات اصلی: برنده علی (200 چیپ)",
                actionLogs = emptyList()
            )
        )
        val buyIns = listOf(
            BuyInRecord(id = "b1", playerId = "p1", playerName = "علی", amount = 1000L, isInitial = true),
            BuyInRecord(id = "b2", playerId = "p2", playerName = "رضا", amount = 1000L, isInitial = true)
        )
        val settings = GameSettings(smallBlind = 25L, bigBlind = 50L, ante = 5L, autoPostBlinds = true, currencyName = "تومان")

        val state = PokerUiState(
            players = players,
            dealerIndex = 1,
            currentStreet = Street.FLOP,
            currentTurnPlayerId = "p1",
            currentHighestBet = 50L,
            minRaise = 50L,
            pots = pots,
            handNumber = 2,
            actionLogs = emptyList(),
            handHistory = handHistory,
            buyInRecords = buyIns,
            chipAdjustments = emptyList(),
            settings = settings,
            isShowdownModalOpen = false,
            isAutoHandOver = false,
            lastHandSummary = "دست قبلی به پایان رسید"
        )

        val json = GameStateSerializer.serialize(state)
        assertTrue(json.isNotEmpty())

        val restored = GameStateSerializer.deserialize(json)
        assertNotNull(restored)
        val r = restored!!
        assertEquals(2, r.players.size)
        assertEquals("علی", r.players[0].name)
        assertEquals(1500L, r.players[0].chips)
        assertEquals(1000L, r.players[0].totalBuyIn)
        assertEquals(1, r.dealerIndex)
        assertEquals(Street.FLOP, r.currentStreet)
        assertEquals("p1", r.currentTurnPlayerId)
        assertEquals(2, r.handNumber)
        assertEquals(1, r.handHistory.size)
        assertEquals(200L, r.handHistory[0].totalPot)
        assertEquals(2, r.buyInRecords.size)
        assertEquals(25L, r.settings.smallBlind)
        assertEquals(50L, r.settings.bigBlind)
        assertEquals("تومان", r.settings.currencyName)
        assertEquals(1, r.pots.size)
        assertEquals(300L, r.pots[0].amount)
    }

    @Test
    fun testRestartStateResetsChipsToTotalBuyInAndClearsHandHistory() {
        val players = listOf(
            Player(id = "p1", name = "علی", seatIndex = 0, chips = 2500L, totalBuyIn = 1000L, currentStreetBet = 50L, totalHandContributed = 100L),
            Player(id = "p2", name = "رضا", seatIndex = 1, chips = 0L, totalBuyIn = 1500L, currentStreetBet = 50L, totalHandContributed = 100L)
        )
        val state = PokerUiState(
            players = players,
            dealerIndex = 1,
            currentStreet = Street.RIVER,
            handNumber = 5,
            handHistory = listOf(
                HandResult(handNumber = 1, totalPot = 200L, pots = emptyList(), playerChipDeltas = emptyMap(), winnersSummary = "win", actionLogs = emptyList())
            )
        )

        // Emulate restart
        val resetPlayers = state.players.map { player ->
            player.copy(
                chips = player.totalBuyIn,
                currentStreetBet = 0L,
                totalHandContributed = 0L,
                status = PlayerHandStatus.ACTIVE,
                hasActedThisRound = false
            )
        }
        val restarted = state.copy(
            players = resetPlayers,
            dealerIndex = 0,
            currentStreet = Street.ENDED,
            currentTurnPlayerId = null,
            currentHighestBet = 0L,
            pots = emptyList(),
            handNumber = 0,
            actionLogs = emptyList(),
            handHistory = emptyList(),
            chipAdjustments = emptyList(),
            lastHandSummary = null,
            isShowdownModalOpen = false,
            isAutoHandOver = false
        )

        assertEquals(0, restarted.handNumber)
        assertTrue(restarted.handHistory.isEmpty())
        assertEquals(Street.ENDED, restarted.currentStreet)
        assertEquals(1000L, restarted.players[0].chips)
        assertEquals(1500L, restarted.players[1].chips)
        assertEquals(PlayerHandStatus.ACTIVE, restarted.players[0].status)
        assertEquals(PlayerHandStatus.ACTIVE, restarted.players[1].status)
    }
}
