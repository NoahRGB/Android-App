package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DeckListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_list_activity)

        val deckListView = findViewById<DeckListView>(R.id.deckListView)

        val db = AppDatabase.getDatabase(this)
        val deckDao = db.deckDao()

        // Insert sample data if the database is empty
        if (deckDao.getAll().isEmpty()) {
            deckDao.insertAll(
                DeckEntity(deckName = "Maths", deckDescription = "Maths revision deck"),
                DeckEntity(deckName = "English", deckDescription = "English revision deck"),
                DeckEntity(deckName = "Science", deckDescription = "Science revision deck")
            )
        }

        val decksFromDb = deckDao.getAll()

        // Map DeckEntity to the Deck data class used by the UI
        val deckList = decksFromDb.map { deckEntity ->
            Deck(deckEntity.id, deckEntity.deckName, deckEntity.deckDescription)
        }

        deckListView.setDecks(deckList)
    }
}