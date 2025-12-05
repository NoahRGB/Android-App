package com.example.app

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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
    var frontText: String,
    var backText: String,
    var rating: String = "neutral", // can be "angry", "neutral", or "smile"
    var lastStudiedDate: String = "01/01/1970"
)

data class DeckWithCards(
    @Embedded val deck: DeckEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<CardEntity>
)

@Dao
interface DeckDao { // interactions with DeckEntity
    @Query("SELECT * FROM DeckEntity")
    fun getAll(): Flow<List<DeckWithCards>>

    @Insert
    suspend fun insertAll(vararg decks: DeckEntity)

    @Query("SELECT * FROM DeckEntity WHERE id = :deckId")
    suspend fun findById(deckId: Int): DeckEntity?

    @Query("SELECT * FROM DeckEntity WHERE id = :deckId")
    fun getDeckWithCardsById(deckId: Int): Flow<DeckWithCards>

    @Delete
    suspend fun delete(deck: DeckEntity)
}

@Dao
interface CardDao { // interactions with CardEntity

    @Insert
    suspend fun insertAll(vararg decks: CardEntity)

    @Query("SELECT * FROM CardEntity")
    fun getAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM CardEntity WHERE deckId = :deckId")
    fun findByDeckId(deckId: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM CardEntity WHERE id = :id")
    suspend fun findById(id: Int): CardEntity?

    @Update
    suspend fun update(card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)

}

@Database(entities = [DeckEntity::class, CardEntity::class], version = 4)
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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}