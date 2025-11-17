package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class DeckEditActivity : AppCompatActivity() {

    private lateinit var cardList: CardListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_edit_activity)

        cardList = findViewById<CardListView>(R.id.cardListView)

        findViewById<ImageButton>(R.id.backToDeckListButton).setOnClickListener {
            finish()
        }

        val deckId = intent.getIntExtra("deckId", -1)

        if (deckId != -1) {
            val db = AppDatabase.getDatabase(this)
            val deckDao = db.deckDao()
            val deckEntity = deckDao.findById(deckId)

            if (deckEntity != null) {
                findViewById<TextView>(R.id.deckTitle).text = deckEntity.deckName
                findViewById<TextView>(R.id.deckDescription).text = deckEntity.deckDescription
            }

            cardList.setDeckId(deckId)
        }
    }
}