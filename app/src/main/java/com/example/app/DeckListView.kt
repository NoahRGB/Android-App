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
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class Deck(var id: Int, var name: String, var description: String, var cardCount: Int) {
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
            toggleAddDeckPopup(!addDeckCardView.isVisible)
        }

        saveNewDeckButton.setOnClickListener {
            val name = newDeckNameEditText.text.toString()
            val description = newDeckDescriptionEditText.text.toString()

            if (name.isNotBlank()) {

                findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    val newDeck = DeckEntity(deckName = name, deckDescription = description)
                    deckDao.insertAll(newDeck)
                }

                newDeckNameEditText.text.clear()
                newDeckDescriptionEditText.text.clear()
                toggleAddDeckPopup(false)
            }
        }

        closeNewDeckPopupButton.setOnClickListener {
            toggleAddDeckPopup(false)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            deckDao.getAll().collect { decksWithCards ->
                val deckList = decksWithCards.map { Deck(it.deck.id, it.deck.deckName, it.deck.deckDescription, it.cards.size) }
                setDecks(deckList)
            }
        }
    }

    private fun setDecks(deckList: List<Deck>) {
        recyclerView.adapter = DeckListAdapter(deckList, onDeckSelected = { selectedDeck ->
            val intent = Intent(context, DeckEditActivity::class.java)
            intent.putExtra("deckId", selectedDeck.id)
            startActivity(context, intent, null)
        }, onDeleteClicked = { deckToDelete ->
            findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                deckDao.delete(DeckEntity(deckToDelete.id, deckToDelete.name, deckToDelete.description))
            }
        })
    }

    private fun toggleAddDeckPopup(show: Boolean) {
        addDeckCardView.visibility = if (show) View.VISIBLE else View.GONE
        blurView.visibility = if (show) View.VISIBLE else View.GONE
    }
}