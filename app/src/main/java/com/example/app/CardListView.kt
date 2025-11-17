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
import kotlinx.coroutines.launch

data class Card(var id: Int, var frontText: String, var backText: String) {
    var cardCount: Int = 0
}

class CardListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private val addCardButton: ImageButton

    private var deckId: Int;

    public fun setDeckId(newDeckId: Int) {
        deckId = newDeckId
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.card_list_view, this, true)
        deckId = 0

        recyclerView = findViewById(R.id.cardListRecyclerView)
        addCardButton = findViewById<ImageButton>(R.id.addCardButton)

        addCardButton.setOnClickListener {
            val intent = Intent(context, AddCardActivity::class.java)
            intent.putExtra("deckId", deckId)
            context.startActivity(intent, null)
        }

        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            val db = AppDatabase.getDatabase(context)
            val card = db.cardDao()

            var cardsFromDb = card.findByDeckId(deckId)
            val cardList = cardsFromDb.map { cardEntity ->
                Card(cardEntity.id, cardEntity.frontText, cardEntity.backText)
            }

            recyclerView.adapter = CardListAdapter(cardList)

        }
    }

}

