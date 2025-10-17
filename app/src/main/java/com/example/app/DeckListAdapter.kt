package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeckListAdapter(private val deckList: List<Deck>, private val onDeckSelected: (Deck) -> Unit) :
    RecyclerView.Adapter<DeckListAdapter.DeckViewHolder>() {

    class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deckNameTextView: TextView = itemView.findViewById(R.id.deckName);
        val deckDescriptionTextView: TextView = itemView.findViewById(R.id.deckDescription);
        val cardCountTextView: TextView = itemView.findViewById(R.id.deckCount);
        val selectButton: Button = itemView.findViewById(R.id.selectDeckButton);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.deck_list_item, parent, false);
        return DeckViewHolder(view);
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = deckList[position];
        holder.deckNameTextView.text = deck.name;
        holder.deckDescriptionTextView.text = deck.description;
        holder.cardCountTextView.text = "${deck.cardCount} cards";

        holder.selectButton.setOnClickListener {
            onDeckSelected(deck);
        }
    }

    override fun getItemCount() = deckList.size;
}
