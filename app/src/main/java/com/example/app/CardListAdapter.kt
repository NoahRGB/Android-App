package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardListAdapter(private val deckList: List<Deck>) :
    RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardFrontText: TextView = itemView.findViewById(R.id.frontText);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false);
        return CardViewHolder(view);
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val deck = deckList[position];
        holder.cardFrontText.text = deck.name;
    }

    override fun getItemCount() = deckList.size;
}
