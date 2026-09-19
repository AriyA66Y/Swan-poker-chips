package com.example.poker.data

import com.example.poker.data.dao.GameDao
import com.example.poker.data.entity.CurrentGameStateEntity
import com.example.poker.data.entity.GameTemplateEntity
import com.example.poker.viewmodel.PokerUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class GameRepository(private val gameDao: GameDao) {

    fun getCurrentGameStateFlow(): Flow<PokerUiState?> {
        return gameDao.getCurrentGameStateFlow().map { entity ->
            entity?.stateJson?.let { GameStateSerializer.deserialize(it) }
        }
    }

    suspend fun loadCurrentGameState(): PokerUiState? = withContext(Dispatchers.IO) {
        val entity = gameDao.getCurrentGameState()
        entity?.stateJson?.let { GameStateSerializer.deserialize(it) }
    }

    suspend fun saveCurrentGameState(state: PokerUiState) = withContext(Dispatchers.IO) {
        val json = GameStateSerializer.serialize(state)
        gameDao.saveCurrentGameState(CurrentGameStateEntity(id = 1, stateJson = json))
    }

    fun getAllTemplatesFlow(): Flow<List<GameTemplateEntity>> {
        return gameDao.getAllTemplatesFlow()
    }

    suspend fun saveTemplate(
        name: String,
        state: PokerUiState,
        existingId: String? = null
    ): String = withContext(Dispatchers.IO) {
        val id = existingId ?: UUID.randomUUID().toString()
        val json = GameStateSerializer.serialize(state)
        val entity = GameTemplateEntity(
            id = id,
            name = name.trim().ifEmpty { "تمپلیت بازی بدون نام" },
            playerCount = state.players.size,
            handsCount = state.handNumber,
            updatedAt = System.currentTimeMillis(),
            stateJson = json
        )
        gameDao.insertOrUpdateTemplate(entity)
        id
    }

    suspend fun loadTemplateState(id: String): PokerUiState? = withContext(Dispatchers.IO) {
        val entity = gameDao.getTemplateById(id)
        entity?.stateJson?.let { GameStateSerializer.deserialize(it) }
    }

    suspend fun deleteTemplate(id: String) = withContext(Dispatchers.IO) {
        gameDao.deleteTemplateById(id)
    }
}
