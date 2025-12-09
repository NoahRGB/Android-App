package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeckListAdapter(
    private val deckList: List<Deck>,
    private val onDeckSelected: (Deck) -> Unit,
    private val onDeleteClicked: (Deck) -> Unit
) :
    RecyclerView.Adapter<DeckListAdapter.DeckViewHolder>() {

    class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // these are all the components that a deck item holds
        val deckNameTextView: TextView = itemView.findViewById(R.id.deckName)
        val deckDescriptionTextView: TextView = itemView.findViewById(R.id.deckDescription)
        val cardCountTextView: TextView = itemView.findViewById(R.id.deckCount)
        val selectButton: Button = itemView.findViewById(R.id.selectDeckButton)
        val deleteDeckButton: ImageButton = itemView.findViewById(R.id.deleteDeckButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.deck_list_item, parent, false)
        return DeckViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        // find the deck and initialise all its properties
        val deck = deckList[position]
        holder.deckNameTextView.text = deck.name
        holder.deckDescriptionTextView.text = deck.description
        holder.cardCountTextView.text = holder.itemView.context.getString(R.string.card_count_text, deck.cardCount)

        holder.selectButton.setOnClickListener {
            onDeckSelected(deck)
        }

        holder.deleteDeckButton.setOnClickListener {
            onDeleteClicked(deck)
        }
    }

    override fun getItemCount() = deckList.size
}
