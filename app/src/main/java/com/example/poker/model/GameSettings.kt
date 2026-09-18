package com.example.poker.model

data class GameSettings(
    val smallBlind: Long = 10L,
    val bigBlind: Long = 20L,
    val ante: Long = 0L,
    val autoPostBlinds: Boolean = true,
    val currencyName: String = "چیپ"
)
