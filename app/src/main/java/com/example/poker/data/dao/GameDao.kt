package com.example.poker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.poker.data.entity.CurrentGameStateEntity
import com.example.poker.data.entity.GameTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM current_game_state WHERE id = 1 LIMIT 1")
    fun getCurrentGameStateFlow(): Flow<CurrentGameStateEntity?>

    @Query("SELECT * FROM current_game_state WHERE id = 1 LIMIT 1")
    suspend fun getCurrentGameState(): CurrentGameStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentGameState(state: CurrentGameStateEntity)

    @Query("SELECT * FROM game_templates ORDER BY updatedAt DESC")
    fun getAllTemplatesFlow(): Flow<List<GameTemplateEntity>>

    @Query("SELECT * FROM game_templates WHERE id = :id LIMIT 1")
    suspend fun getTemplateById(id: String): GameTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTemplate(template: GameTemplateEntity)

    @Query("DELETE FROM game_templates WHERE id = :id")
    suspend fun deleteTemplateById(id: String)
}
