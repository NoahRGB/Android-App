package com.example.app

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var saveNewCardButton: Button
    private var flipCardButton: ImageButton
    private var flashcardFront: EditText
    private var flashcardBack: EditText
    private var isFrontVisible = true
    private val db = AppDatabase.getDatabase(context)
    private val cardDao = db.cardDao()
    var deckId: Int = -1

    init {
        LayoutInflater.from(context).inflate(R.layout.add_card_view, this, true)
        flipCardButton = findViewById<ImageButton>(R.id.flipCardButton)
        saveNewCardButton = findViewById<Button>(R.id.saveNewCardButton)
        flashcardFront = findViewById<EditText>(R.id.flashcardFront)
        flashcardBack = findViewById<EditText>(R.id.flashcardBack)

        // gather various screen based metrics/values
        // that are used in the flip animation
        val density = resources.displayMetrics.density
        val heightInDp = 300
        val heightInPixels = (heightInDp * density).toInt()
        var layoutParams = flashcardFront.layoutParams
        layoutParams.height = heightInPixels
        flashcardFront.layoutParams = layoutParams
        layoutParams = flashcardBack.layoutParams
        layoutParams.height = heightInPixels
        flashcardBack.layoutParams = layoutParams

        // sets camera distance so flip is completely visible
        AnimationUtils.setupCardFlip(flashcardFront)
        AnimationUtils.setupCardFlip(flashcardBack)

        flipCardButton.setOnClickListener {
            flipCard()
        }

        saveNewCardButton.setOnClickListener {
            val frontText = flashcardFront.text.toString()
            val backText = flashcardBack.text.toString()

            if (frontText.isNotBlank() && backText.isNotBlank()) {
                // launch coroutine, insert new card into DB
                findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    val newCard = CardEntity(deckId=deckId, frontText=frontText, backText=backText)
                    cardDao.insertAll(newCard)
                }
                // clear text and flip so another flashcard
                // can be added
                flashcardFront.text.clear()
                flashcardBack.text.clear()
                if (!isFrontVisible) {
                    flipCard()
                }
            }
        }
    }

    private fun flipCard() {
        val (outView, inView) = if (isFrontVisible) {
            flashcardFront to flashcardBack
        } else {
            flashcardBack to flashcardFront
        }
        AnimationUtils.flipCard(outView, inView)
        isFrontVisible = !isFrontVisible
    }
}