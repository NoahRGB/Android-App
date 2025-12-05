package com.example.app

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class Card(var id: Int, var frontText: String, var backText: String, var isFrontVisible: Boolean = true) {
    var cardCount: Int = 0
}

class CardListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private val addCardButton: ImageButton
    private val db = AppDatabase.getDatabase(context)
    private val cardDao = db.cardDao()

    private var deckId: Int = -1

    fun setDeckId(newDeckId: Int) {
        deckId = newDeckId
        if (deckId != -1) {
            findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                cardDao.findByDeckId(deckId).collect { cardsFromDb ->
                    val cardList = cardsFromDb.map { cardEntity ->
                        Card(cardEntity.id, cardEntity.frontText, cardEntity.backText)
                    }
                    recyclerView.adapter = CardListAdapter(cardList, onDeleteClicked = { cardToDelete ->
                        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                            cardDao.delete(CardEntity(cardToDelete.id, deckId, cardToDelete.frontText, cardToDelete.backText))
                        }
                    }, onEditClicked = { cardToEdit ->
                        val intent = Intent(context, EditCardActivity::class.java)
                        intent.putExtra("cardId", cardToEdit.id)
                        context.startActivity(intent)
                    })
                }
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.card_list_view, this, true)

        recyclerView = findViewById(R.id.cardListRecyclerView)
        addCardButton = findViewById<ImageButton>(R.id.addCardButton)

        addCardButton.setOnClickListener {
            if (deckId != -1) {
                val intent = Intent(context, AddCardActivity::class.java)
                intent.putExtra("deckId", deckId)
                context.startActivity(intent, null)
            }
        }
    }

}