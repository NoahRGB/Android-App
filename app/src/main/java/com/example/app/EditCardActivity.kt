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
    private lateinit var flipButton: ImageButton
    private lateinit var flashcardFront: EditText
    private lateinit var flashcardBack: EditText

    private var isFrontVisible = true
    private var card: CardEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        backButton = findViewById(R.id.backToDeckEditButton)
        saveButton = findViewById(R.id.saveCardButton)
        flipButton = findViewById(R.id.flipCardButton)

        // Get a reference to the included flashcardView
        val flashcardView = findViewById<View>(R.id.flashcardView)

        flashcardFront = flashcardView.findViewById(R.id.flashcardFront)
        flashcardBack = flashcardView.findViewById(R.id.flashcardBack)

        // Set initial visibility
        flashcardFront.visibility = View.VISIBLE
        flashcardBack.visibility = View.INVISIBLE

        AnimationUtils.setupCardFlip(flashcardFront)
        AnimationUtils.setupCardFlip(flashcardBack)

        val cardId = intent.getIntExtra("cardId", -1)

        val db = AppDatabase.getDatabase(this)
        val cardDao = db.cardDao()

        lifecycleScope.launch {
            card = cardDao.findById(cardId)
            card?.let {
                flashcardFront.setText(it.frontText)
                flashcardBack.setText(it.backText)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            val frontText = flashcardFront.text.toString()
            val backText = flashcardBack.text.toString()

            lifecycleScope.launch {
                card?.let {
                    it.frontText = frontText
                    it.backText = backText
                    cardDao.update(it)
                    finish()
                }
            }
        }

        flipButton.setOnClickListener {
            val (outView, inView) = if (isFrontVisible) {
                flashcardFront to flashcardBack
            } else {
                flashcardBack to flashcardFront
            }
            AnimationUtils.flipCard(outView, inView)
            isFrontVisible = !isFrontVisible
        }
    }
}