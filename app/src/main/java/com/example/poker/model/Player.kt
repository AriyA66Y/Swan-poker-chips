package com.example.poker.model

import java.util.UUID

enum class PlayerHandStatus(val faTitle: String) {
    ACTIVE("فعال"),
    FOLDED("فولد شده"),
    ALL_IN("آل‌این"),
    SITTING_OUT("خارج از بازی")
}

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val seatIndex: Int,
    val chips: Long,
    val totalBuyIn: Long,
    val currentStreetBet: Long = 0L,
    val totalHandContributed: Long = 0L,
    val status: PlayerHandStatus = PlayerHandStatus.ACTIVE,
    val colorHex: Long = 0xFF1E88E5L,
    val hasActedThisRound: Boolean = false
) {
    val netProfitLoss: Long
        get() = chips - totalBuyIn

    val roiPercentage: Float
        get() = if (totalBuyIn > 0) ((chips - totalBuyIn).toFloat() / totalBuyIn.toFloat()) * 100f else 0f

    val isEligibleForPot: Boolean
        get() = status != PlayerHandStatus.FOLDED && status != PlayerHandStatus.SITTING_OUT && totalHandContributed > 0
}

data class BuyInRecord(
    val id: String = UUID.randomUUID().toString(),
    val playerId: String,
    val playerName: String,
    val amount: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val isInitial: Boolean = false
)

data class ChipAdjustment(
    val id: String = UUID.randomUUID().toString(),
    val playerId: String,
    val playerName: String,
    val delta: Long,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)
