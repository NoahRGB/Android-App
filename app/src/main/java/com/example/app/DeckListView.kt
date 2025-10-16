package com.example.app

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Deck(var name: String, var description: String) {
    var cardCount: Int = 0;
}

class DeckListView : ConstraintLayout {

    private val recyclerView: RecyclerView;

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.deck_list_view, this, true);
        recyclerView = findViewById(R.id.deckListRecyclerView);
    }

    fun setDecks(deckList: List<Deck>) {
        recyclerView.adapter = DeckListAdapter(deckList);
    }
}