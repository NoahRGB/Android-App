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

        val decksFromDb = deckDao.getAll()

        val deckList = decksFromDb.map { deckEntity ->
            Deck(deckEntity.id, deckEntity.deckName, deckEntity.deckDescription)
        }

        deckListView.setDecks(deckList)
    }
}