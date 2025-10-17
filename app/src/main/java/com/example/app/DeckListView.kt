package com.example.app

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible

data class Deck(var id: Int, var name: String, var description: String) {
    var cardCount: Int = 0
}

class DeckListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private val addDeckButton: ImageButton
    private val addDeckCardView: CardView
    private val newDeckNameEditText: EditText
    private val newDeckDescriptionEditText: EditText
    private val saveNewDeckButton: Button
    private val closeNewDeckPopupButton: ImageButton
    private val blurView: View
    private val db = AppDatabase.getDatabase(context)
    private val deckDao = db.deckDao()

    init {
        LayoutInflater.from(context).inflate(R.layout.deck_list_view, this, true)
        recyclerView = findViewById(R.id.deckListRecyclerView)
        addDeckButton = findViewById(R.id.addDeckButton)
        addDeckCardView = findViewById(R.id.addDeckCardView)
        newDeckNameEditText = findViewById(R.id.newDeckNameEditText)
        newDeckDescriptionEditText = findViewById(R.id.newDeckDescriptionEditText)
        saveNewDeckButton = findViewById(R.id.saveNewDeckButton)
        closeNewDeckPopupButton = findViewById(R.id.closeNewDeckPopup)
        blurView = findViewById(R.id.blurView)

        addDeckButton.setOnClickListener {
            if (addDeckCardView.isVisible) {
                addDeckCardView.visibility = View.GONE
                blurView.visibility = View.GONE
            } else {
                addDeckCardView.visibility = View.VISIBLE
                blurView.visibility = View.VISIBLE
            }
        }

        saveNewDeckButton.setOnClickListener {
            val name = newDeckNameEditText.text.toString()
            val description = newDeckDescriptionEditText.text.toString()

            if (name.isNotBlank()) {
                val newDeck = DeckEntity(deckName = name, deckDescription = description)
                deckDao.insertAll(newDeck)

                // Refresh the list
                val updatedDecks = deckDao.getAll().map { Deck(it.id, it.deckName, it.deckDescription) }
                setDecks(updatedDecks)

                // Clear input and hide card
                newDeckNameEditText.text.clear()
                newDeckDescriptionEditText.text.clear()
                addDeckCardView.visibility = View.GONE
                blurView.visibility = View.GONE
            }
        }

        closeNewDeckPopupButton.setOnClickListener {
            addDeckCardView.visibility = View.GONE
            blurView.visibility = View.GONE
        }
    }

    fun setDecks(deckList: List<Deck>) {
        recyclerView.adapter = DeckListAdapter(deckList) { selectedDeck ->
            val intent = Intent(context, DeckEditActivity::class.java)
            intent.putExtra("deckId", selectedDeck.id)
            startActivity(context, intent, null)
        }
    }
}