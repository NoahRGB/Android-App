package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardListAdapter(
    private val cardList: List<Card>,
    private val onDeleteClicked: (Card) -> Unit,
    private val onEditClicked: (Card) -> Unit
) :
    RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // these are all the components that each card in the card list owns
        val cardFrontText: TextView = itemView.findViewById(R.id.flashcardFront)
        val cardBackText: TextView = itemView.findViewById(R.id.flashcardBack)
        val flipCardButton: ImageButton = itemView.findViewById(R.id.flipCardButton)
        val deleteCardButton: ImageButton = itemView.findViewById(R.id.deleteCardButton)
        val editCardButton: ImageButton = itemView.findViewById(R.id.editCardButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // find the card's details and initialise various properties
        val card = cardList[position]
        holder.cardFrontText.text = card.frontText
        holder.cardBackText.text = card.backText

        // set initial visibility based on the card's state
        if (card.isFrontVisible) {
            holder.cardFrontText.visibility = View.VISIBLE
            holder.cardBackText.visibility = View.INVISIBLE
        } else {
            holder.cardFrontText.visibility = View.INVISIBLE
            holder.cardBackText.visibility = View.VISIBLE
        }

        AnimationUtils.setupCardFlip(holder.cardFrontText)
        AnimationUtils.setupCardFlip(holder.cardBackText)

        // when the flip button is pressed, switch the text
        // and call the flip animation from AnimationUtils
        holder.flipCardButton.setOnClickListener {
            val (outView, inView) = if (card.isFrontVisible) {
                holder.cardFrontText to holder.cardBackText
            } else {
                holder.cardBackText to holder.cardFrontText
            }
            AnimationUtils.flipCard(outView, inView)
            card.isFrontVisible = !card.isFrontVisible
        }

        holder.deleteCardButton.setOnClickListener {
            onDeleteClicked(card)
        }

        holder.editCardButton.setOnClickListener {
            onEditClicked(card)
        }
    }

    override fun getItemCount() = cardList.size
}