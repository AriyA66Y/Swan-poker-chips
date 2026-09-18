package com.example.poker.model

import java.util.UUID

enum class Street(val faName: String, val enName: String) {
    PREFLOP("پری‌فلاپ", "Preflop"),
    FLOP("فلاپ", "Flop"),
    TURN("تِرن", "Turn"),
    RIVER("ریور", "River"),
    SHOWDOWN("تعیین برنده", "Showdown"),
    ENDED("پایان دست", "Ended")
}

enum class ActionType(val faTitle: String) {
    POST_SB("ثبت اسمال بلایند"),
    POST_BB("ثبت بیگ بلایند"),
    POST_ANTE("ثبت آنته"),
    CHECK("چک"),
    CALL("کال"),
    BET("بت"),
    RAISE("ریز"),
    ALL_IN("آل‌این"),
    FOLD("فولد")
}

data class PlayerAction(
    val id: String = UUID.randomUUID().toString(),
    val playerId: String,
    val playerName: String,
    val actionType: ActionType,
    val amount: Long = 0L,
    val street: Street,
    val timestamp: Long = System.currentTimeMillis()
)

data class Pot(
    val id: String = UUID.randomUUID().toString(),
    val name: String, // "پات اصلی (Main Pot)" or "ساید پات ۱ (Side Pot 1)"
    val amount: Long,
    val eligiblePlayerIds: List<String>,
    val winners: List<String> = emptyList()
)

data class HandResult(
    val id: String = UUID.randomUUID().toString(),
    val handNumber: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val totalPot: Long,
    val pots: List<Pot>,
    val playerChipDeltas: Map<String, Long>, // Player ID to Net Change in this hand
    val winnersSummary: String,
    val actionLogs: List<PlayerAction> = emptyList()
)
