package com.example.app

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

// database entity for storing a deck of flashcards
// has a one-to-many relationship with CardEntity
// ONE deck can have MANY cards
@Entity
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckName: String,
    val deckDescription: String
)

// database entity for storing details of individual flashcards
// has a one-to-many relationship with CardEntity
// ONE deck can have MANY cards
@Entity(
    foreignKeys = [ForeignKey(
        entity = DeckEntity::class,
        parentColumns = ["id"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int, // foreign key to DeckEntity primary key
    val frontText: String,
    val backText: String
)

@Dao
interface DeckDao { // interactions with DeckEntity
    @Query("SELECT * FROM DeckEntity")
    suspend fun getAll(): List<DeckEntity>

    @Insert
    fun insertAll(vararg decks: DeckEntity)

    @Query("SELECT * FROM DeckEntity WHERE id = :deckId")
    fun findById(deckId: Int): DeckEntity?
}

@Dao
interface CardDao { // interactions with CardEntity

    @Query("SELECT * FROM CardEntity WHERE deckId = :deckId")
    fun findByDeckId(deckId: Int): List<CardEntity>

}

@Database(entities = [DeckEntity::class, CardEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

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
                .allowMainThreadQueries()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}