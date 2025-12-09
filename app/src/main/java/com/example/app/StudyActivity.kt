package com.example.app

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class StudyActivity : AppCompatActivity() {

    private lateinit var flashcardFront: EditText
    private lateinit var flashcardBack: EditText
    private lateinit var flipButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var deckName: TextView
    private lateinit var angryButton: ImageButton
    private lateinit var neutralButton: ImageButton
    private lateinit var smileButton: ImageButton

    private var cardList: MutableList<CardEntity> = mutableListOf()
    private var isFront = true

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        val flashcardView = findViewById<View>(R.id.flashcardView)
        flashcardFront = flashcardView.findViewById(R.id.flashcardFront)
        flashcardBack = flashcardView.findViewById(R.id.flashcardBack)
        flipButton = findViewById(R.id.flipCardButton)
        backButton = findViewById(R.id.backButton)
        deckName = findViewById(R.id.deckName)
        angryButton = findViewById(R.id.angryButton)
        neutralButton = findViewById(R.id.neutralButton)
        smileButton = findViewById(R.id.smileButton)

        flashcardFront.isFocusable = false
        flashcardBack.isFocusable = false
        AnimationUtils.setupCardFlip(flashcardFront)
        AnimationUtils.setupCardFlip(flashcardBack)

        val deckId = intent.getIntExtra("deckId", -1)

        db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            // gather all cards and sort them correctly by rating and the last time they were studied
            val cardDao = db.cardDao()
            val deckDao = db.deckDao()

            val deck = deckDao.findById(deckId)
            deck?.let {
                deckName.text = it.deckName
            }

            // parse the date and ratings
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            val initialList = cardDao.findByDeckId(deckId).first().sortedWith(compareBy<CardEntity> {
                when (it.rating) {
                    "angry" -> 0
                    "neutral" -> 1
                    "smile" -> 2
                    else -> 3
                }
            }.thenBy {
                try {
                    LocalDate.parse(it.lastStudiedDate, formatter)
                } catch (e: Exception) {
                    LocalDate.MIN
                }
            })
            cardList.addAll(initialList)
            showCard()
        }

        flipButton.setOnClickListener {
            flipCard()
        }

        backButton.setOnClickListener {
            finish()
        }

        // various types of card rating
        angryButton.setOnClickListener {
            rateCard("angry")
        }

        neutralButton.setOnClickListener {
            rateCard("neutral")
        }

        smileButton.setOnClickListener {
            rateCard("smile")
        }
    }

    private fun showCard() {
        if (cardList.isNotEmpty()) {
            // gather the new card and show its front text
            val card = cardList[0]
            flashcardFront.setText(card.frontText)
            flashcardBack.setText(card.backText)

            flashcardFront.visibility = View.VISIBLE
            flashcardBack.visibility = View.INVISIBLE
        } else {
            // handle case where deck is empty or all cards have been studied and removed
            finish()
        }
    }

    private fun flipCard() {
        // flip to back using AnimationUtils
        val (outView, inView) = if (isFront) {
            flashcardFront to flashcardBack
        } else {
            flashcardBack to flashcardFront
        }
        AnimationUtils.flipCard(outView, inView)
        isFront = !isFront
    }

    private fun nextCard() {
        if (cardList.isNotEmpty()) {
            // turn the card back to the front for the next card
            if (!isFront) {
                flipCard()
            }

            // remove the card the user just studied so it can be repositioned
            val studiedCard = cardList.removeAt(0)

            // reinsert the card into the list based on the size of the list and the rating it got
            when (studiedCard.rating) {
                "angry" -> {
                    // add it back soon since the user is not confident with it
                    val reinsertPosition = if (cardList.size >= 3) 3 else cardList.size
                    cardList.add(reinsertPosition, studiedCard)
                }
                "neutral" -> {
                    // add it relatively soon since the user is not sure
                    val reinsertPosition = (cardList.size / 2).coerceAtMost(cardList.size)
                    cardList.add(reinsertPosition, studiedCard)
                }
                "smile" -> {
                    // add it to the back since the user is confident with this card
                    cardList.add(studiedCard)
                }
                else -> {
                    cardList.add(studiedCard)
                }
            }

            isFront = true
            showCard()
        }
    }

    private fun rateCard(rating: String) {
        if (cardList.isNotEmpty()) {
            val card = cardList[0]
            card.rating = rating

            // parse the current date
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            val today = LocalDate.now()
            val todayStr = today.format(formatter)
            card.lastStudiedDate = todayStr

            // update the user's streak (so it can be shown in AboutMeActivity)
            val prefs = getSharedPreferences("StreakInfo", Context.MODE_PRIVATE)
            val lastStudiedDateStr = prefs.getString("lastStudiedDate", null)
            var streakCount = prefs.getInt("streakCount", 0)

            if (lastStudiedDateStr != todayStr) { // only update streak once a day
                if (lastStudiedDateStr != null) {
                    val lastDate = LocalDate.parse(lastStudiedDateStr, formatter)

                    if (lastDate.plusDays(1).isEqual(today)) {
                        // add to streak
                        streakCount++
                    } else {
                        // reset streak
                        streakCount = 1
                    }
                } else {
                    // initialise streak
                    streakCount = 1
                }

                // save in the preferences
                prefs.edit()
                    .putInt("streakCount", streakCount)
                    .putString("lastStudiedDate", todayStr)
                    .apply()
            }

            lifecycleScope.launch {
                db.cardDao().update(card)
                nextCard()
            }
        }
    }
}