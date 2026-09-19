package com.example.poker.model

import java.util.UUID

enum class Street(val faName: String, val enName: String) {
    PREFLOP("پری‌فلاپ", "Preflop"),
    FLOP("فلاپ", "Flop"),
    TURN("تِرن", "Turn"),
    RIVER("ریور", "River"),
    SHOWDOWN("تعیین برنده", "Showdown"),
    ENDED("پایان دست", "Ended");

    fun getName(lang: String): String = if (lang == "en") enName else faName
}

enum class ActionType(val faTitle: String, val enTitle: String) {
    POST_SB("ثبت اسمال بلایند", "Post SB"),
    POST_BB("ثبت بیگ بلایند", "Post BB"),
    POST_ANTE("ثبت آنته", "Post Ante"),
    CHECK("چک", "Check"),
    CALL("کال", "Call"),
    BET("بت", "Bet"),
    RAISE("ریز", "Raise"),
    ALL_IN("آل‌این", "All-In"),
    FOLD("فولد", "Fold");

    fun getTitle(lang: String): String = if (lang == "en") enTitle else faTitle
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
