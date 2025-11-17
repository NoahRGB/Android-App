package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DeckListActivity : AppCompatActivity() {

    private lateinit var accountIcon: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_list_activity)

        accountIcon = findViewById(R.id.accountIcon)
        val deckListView = findViewById<DeckListView>(R.id.deckListView)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@DeckListActivity)
            val deckDao = db.deckDao()

            val decksFromDb = deckDao.getAll()

            val deckList = decksFromDb.map { deckEntity ->
                Deck(deckEntity.id, deckEntity.deckName, deckEntity.deckDescription)
            }

            deckListView.setDecks(deckList)
        }

        accountIcon.setOnClickListener {
            val intent = Intent(this, AboutMeActivity::class.java)
            startActivity(intent)
        }

    }
}