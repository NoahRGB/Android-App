package com.example.app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddCardActivity : AppCompatActivity() {

    private lateinit var deckTitle: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_card_activity)

        deckTitle = findViewById<TextView>(R.id.deckTitle)
        backButton = findViewById<ImageButton>(R.id.backToDeckEditButton)

        val deckId = intent.getIntExtra("deckId", -1)

        lifecycleScope.launch {
            // get decks from db to update the deck count
            val db = AppDatabase.getDatabase(this@AddCardActivity)
            val deckDao = db.deckDao()
            val deckName = deckDao.findById(deckId)

            // update the deck count text
            deckTitle.text = "${deckName?.deckName}"
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}