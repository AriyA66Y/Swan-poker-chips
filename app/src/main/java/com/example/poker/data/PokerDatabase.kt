package com.example.poker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.poker.data.dao.GameDao
import com.example.poker.data.entity.CurrentGameStateEntity
import com.example.poker.data.entity.GameTemplateEntity

@Database(
    entities = [CurrentGameStateEntity::class, GameTemplateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokerDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: PokerDatabase? = null

        fun getInstance(context: Context): PokerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokerDatabase::class.java,
                    "poker_chip_counter.db"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
