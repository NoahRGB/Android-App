package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeckEditActivity : AppCompatActivity() {

    private lateinit var cardList: CardListView
    private var deckId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_edit_activity)

        cardList = findViewById(R.id.cardListView)

        findViewById<ImageButton>(R.id.backToDeckListButton).setOnClickListener {
            finish() // return back to the deck list
        }

        findViewById<Button>(R.id.studyDeckButton).setOnClickListener {
            // start a 'study session'
            val intent = Intent(this, StudyActivity::class.java)
            intent.putExtra("deckId", deckId)
            startActivity(intent)
        }

        deckId = intent.getIntExtra("deckId", -1)
        cardList.setDeckId(deckId)
    }

    override fun onResume() {
        super.onResume()
        loadDeckData()
    }

    private fun loadDeckData() {
        if (deckId != -1) {
            val db = AppDatabase.getDatabase(this)
            val deckDao = db.deckDao()
            // load all the deck information from the DB in a coroutine
            lifecycleScope.launch {
                deckDao.getDeckWithCardsById(deckId).first()?.let { deckWithCards ->
                    findViewById<TextView>(R.id.deckTitle).text = deckWithCards.deck.deckName
                    findViewById<TextView>(R.id.deckDescription).text = deckWithCards.deck.deckDescription
                    findViewById<TextView>(R.id.deckCardCount).text = "${deckWithCards.cards.size} cards"
                }
            }
        }
    }
}