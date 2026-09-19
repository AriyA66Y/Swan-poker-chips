package com.example.poker

import com.example.poker.logic.PotCalculator
import com.example.poker.model.Player
import com.example.poker.model.PlayerHandStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PotCalculatorTest {

    @Test
    fun testSimplePotEqualContributions() {
        val p1 = Player(id = "1", name = "Ali", seatIndex = 0, chips = 900, totalBuyIn = 1000, totalHandContributed = 100)
        val p2 = Player(id = "2", name = "Reza", seatIndex = 1, chips = 900, totalBuyIn = 1000, totalHandContributed = 100)
        val p3 = Player(id = "3", name = "Sara", seatIndex = 2, chips = 900, totalBuyIn = 1000, totalHandContributed = 100)

        val (pots, refunds) = PotCalculator.calculatePots(listOf(p1, p2, p3))
        assertEquals(1, pots.size)
        assertEquals(300L, pots[0].amount)
        assertEquals(3, pots[0].eligiblePlayerIds.size)
        assertTrue(refunds.isEmpty())
    }

    @Test
    fun testSidePotWithAllInPlayer() {
        // Player 1 has 50 chips (all-in)
        // Player 2 has 150 chips (all-in)
        // Player 3 bets 150 (active)
        val p1 = Player(id = "1", name = "Ali", seatIndex = 0, chips = 0, totalBuyIn = 50, totalHandContributed = 50, status = PlayerHandStatus.ALL_IN)
        val p2 = Player(id = "2", name = "Reza", seatIndex = 1, chips = 0, totalBuyIn = 150, totalHandContributed = 150, status = PlayerHandStatus.ALL_IN)
        val p3 = Player(id = "3", name = "Sara", seatIndex = 2, chips = 350, totalBuyIn = 500, totalHandContributed = 150, status = PlayerHandStatus.ACTIVE)

        val (pots, refunds) = PotCalculator.calculatePots(listOf(p1, p2, p3))
        // Main pot: 50 * 3 = 150 (Eligible: 1, 2, 3)
        // Side pot 1: (150 - 50) * 2 = 200 (Eligible: 2, 3)
        assertEquals(2, pots.size)
        assertEquals(150L, pots[0].amount)
        assertEquals(listOf("1", "2", "3"), pots[0].eligiblePlayerIds)

        assertEquals(200L, pots[1].amount)
        assertEquals(listOf("2", "3"), pots[1].eligiblePlayerIds)
        assertTrue(refunds.isEmpty())
    }

    @Test
    fun testUncalledExcessBetRefund() {
        // Player 1 has 50 (all-in)
        // Player 2 bets 100 (uncalled 50)
        val p1 = Player(id = "1", name = "Ali", seatIndex = 0, chips = 0, totalBuyIn = 50, totalHandContributed = 50, status = PlayerHandStatus.ALL_IN)
        val p2 = Player(id = "2", name = "Reza", seatIndex = 1, chips = 400, totalBuyIn = 500, totalHandContributed = 100, status = PlayerHandStatus.ACTIVE)

        val (pots, refunds) = PotCalculator.calculatePots(listOf(p1, p2))
        assertEquals(1, pots.size)
        assertEquals(100L, pots[0].amount) // 50 from Ali + 50 from Reza
        assertEquals(listOf("1", "2"), pots[0].eligiblePlayerIds)
        assertEquals(50L, refunds["2"]) // Reza gets 50 refunded
    }

    @Test
    fun testFoldedPlayerMoneyRemainsInPot() {
        // Player 1 contributed 50 then folded
        // Player 2 contributed 100
        // Player 3 contributed 100
        val p1 = Player(id = "1", name = "Ali", seatIndex = 0, chips = 950, totalBuyIn = 1000, totalHandContributed = 50, status = PlayerHandStatus.FOLDED)
        val p2 = Player(id = "2", name = "Reza", seatIndex = 1, chips = 900, totalBuyIn = 1000, totalHandContributed = 100, status = PlayerHandStatus.ACTIVE)
        val p3 = Player(id = "3", name = "Sara", seatIndex = 2, chips = 900, totalBuyIn = 1000, totalHandContributed = 100, status = PlayerHandStatus.ACTIVE)

        val (pots, _) = PotCalculator.calculatePots(listOf(p1, p2, p3))
        // Ali's 50 is in the pot, but only Reza and Sara can win it! Total: 50 + 100 + 100 = 250
        assertEquals(1, pots.size)
        assertEquals(250L, pots[0].amount)
        assertEquals(listOf("2", "3"), pots[0].eligiblePlayerIds)
    }

    @Test
    fun testDeterministicPotIds() {
        val p1 = Player(id = "1", name = "Ali", seatIndex = 0, chips = 0, totalBuyIn = 50, totalHandContributed = 50, status = PlayerHandStatus.ALL_IN)
        val p2 = Player(id = "2", name = "Reza", seatIndex = 1, chips = 0, totalBuyIn = 150, totalHandContributed = 150, status = PlayerHandStatus.ALL_IN)
        val p3 = Player(id = "3", name = "Sara", seatIndex = 2, chips = 350, totalBuyIn = 500, totalHandContributed = 150, status = PlayerHandStatus.ACTIVE)

        val (pots, _) = PotCalculator.calculatePots(listOf(p1, p2, p3))
        assertEquals(2, pots.size)
        assertEquals("pot_0", pots[0].id)
        assertEquals("pot_1", pots[1].id)
    }

    @Test
    fun testSplitPotDistribution() {
        val payouts = PotCalculator.splitPot(300L, listOf("1", "2"))
        assertEquals(150L, payouts["1"])
        assertEquals(150L, payouts["2"])

        // Odd amount split: 101 split between 2 players gives 51 and 50
        val oddPayouts = PotCalculator.splitPot(101L, listOf("1", "2"))
        assertEquals(101L, (oddPayouts["1"] ?: 0L) + (oddPayouts["2"] ?: 0L))
    }
}
