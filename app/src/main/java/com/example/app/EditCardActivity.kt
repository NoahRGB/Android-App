package com.example.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditCardActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var flashcardFront: EditText
    private lateinit var flashcardBack: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        backButton = findViewById(R.id.backToDeckEditButton)
        saveButton = findViewById(R.id.saveCardButton)

        // Get a reference to the included flashcardView
        val flashcardView = findViewById<View>(R.id.flashcardView)

        flashcardFront = flashcardView.findViewById(R.id.flashcardFront)
        flashcardBack = flashcardView.findViewById(R.id.flashcardBack)

        val cardId = intent.getIntExtra("cardId", -1)

        val db = AppDatabase.getDatabase(this)
        val cardDao = db.cardDao()

        lifecycleScope.launch {
            val card = cardDao.findById(cardId)
            if (card != null) {
                flashcardFront.setText(card.frontText)
                flashcardBack.setText(card.backText)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            val frontText = flashcardFront.text.toString()
            val backText = flashcardBack.text.toString()

            lifecycleScope.launch {
                val card = cardDao.findById(cardId)
                if (card != null) {
                    card.frontText = frontText
                    card.backText = backText
                    cardDao.update(card)
                    finish()
                }
            }
        }
    }
}
