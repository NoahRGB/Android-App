package com.example.app

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckName: String,
    val deckDescription: String
)

@Dao
interface DeckDao {
    @Query("SELECT * FROM DeckEntity")
    fun getAll(): List<DeckEntity>

    @Insert
    fun insertAll(vararg decks: DeckEntity)

    @Query("SELECT * FROM DeckEntity WHERE id = :deckId")
    fun findById(deckId: Int): DeckEntity?
}

@Database(entities = [DeckEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                // WARNING: This is not recommended for production apps.
                // We're allowing main thread queries for simplicity.
                .allowMainThreadQueries()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}