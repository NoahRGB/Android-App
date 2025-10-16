package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DeckListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.deck_list_activity);

        val deckListView = findViewById<DeckListView>(R.id.deckListView);

        // get this deck list from local storage somehow ?
        val deckList = mutableListOf(Deck("Maths", "Maths revision deck"), Deck("English", "English revision deck"));

        deckListView.setDecks(deckList);

    }

}