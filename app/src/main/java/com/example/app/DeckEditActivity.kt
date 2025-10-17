package com.example.app

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class DeckEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_edit_activity)

        val deckId = intent.getIntExtra("deckId", -1)

        if (deckId != -1) {
            val db = AppDatabase.getDatabase(this)
            val deckDao = db.deckDao()
            val deckEntity = deckDao.findById(deckId)

            if (deckEntity != null) {
                findViewById<EditText>(R.id.deckName).setText(deckEntity.deckName)
                findViewById<EditText>(R.id.deckDescription).setText(deckEntity.deckDescription)
            }
        }
    }
}