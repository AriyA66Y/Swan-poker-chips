package com.example.poker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_game_state")
data class CurrentGameStateEntity(
    @PrimaryKey val id: Int = 1,
    val stateJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)
