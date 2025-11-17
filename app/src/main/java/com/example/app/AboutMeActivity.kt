package com.example.app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AboutMeActivity : AppCompatActivity() {

    private lateinit var closeNewDeckPopupButton: ImageButton
    private lateinit var deckCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_me_activity)

        closeNewDeckPopupButton = findViewById(R.id.closeAboutMeButton)
        deckCountText = findViewById(R.id.deckCountText)

        // return to previous activity if back button is pressed
        closeNewDeckPopupButton.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            // get decks from db to update the deck count
            val db = AppDatabase.getDatabase(this@AboutMeActivity)
            val deckDao = db.deckDao()
            val decksFromDb = deckDao.getAll()

            // update the deck count text
            deckCountText.text = "Decks: ${decksFromDb.size.toString()}"
        }
    }
}