package com.example.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class CardListAdapter(
    private val cardList: List<Card>,
    private val onDeleteClicked: (Card) -> Unit,
    private val onEditClicked: (Card) -> Unit
) :
    RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardFrontText: EditText = itemView.findViewById(R.id.flashcardFront)
        val cardBackText: EditText = itemView.findViewById(R.id.flashcardBack)
        val flipCardButton: ImageButton = itemView.findViewById(R.id.flipCardButton)
        val deleteCardButton: ImageButton = itemView.findViewById(R.id.deleteCardButton)
        val editCardButton: ImageButton = itemView.findViewById(R.id.editCardButton)
        var isFlipping = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.cardFrontText.setText(card.frontText)
        holder.cardBackText.setText(card.backText)

        // Set initial visibility based on the card's state
        if (card.isFrontVisible) {
            holder.cardFrontText.visibility = View.VISIBLE
            holder.cardBackText.visibility = View.INVISIBLE
        } else {
            holder.cardFrontText.visibility = View.INVISIBLE
            holder.cardBackText.visibility = View.VISIBLE
        }

        // Disable editing
        holder.cardFrontText.isEnabled = false
        holder.cardBackText.isEnabled = false

        val scale = holder.itemView.context.resources.displayMetrics.density
        holder.cardFrontText.cameraDistance = 8000 * scale
        holder.cardBackText.cameraDistance = 8000 * scale

        holder.flipCardButton.setOnClickListener {
            if (!holder.isFlipping) {
                flipCard(holder, card)
            }
        }

        holder.deleteCardButton.setOnClickListener {
            onDeleteClicked(card)
        }

        holder.editCardButton.setOnClickListener {
            onEditClicked(card)
        }
    }

    override fun getItemCount() = cardList.size

    private fun flipCard(holder: CardViewHolder, card: Card) {
        holder.isFlipping = true
        val visibleView = if (card.isFrontVisible) holder.cardFrontText else holder.cardBackText
        val invisibleView = if (card.isFrontVisible) holder.cardBackText else holder.cardFrontText

        val outAnimator = ObjectAnimator.ofFloat(visibleView, "rotationY", 0f, 90f).apply {
            duration = 250
            interpolator = AccelerateInterpolator()
        }

        val inAnimator = ObjectAnimator.ofFloat(invisibleView, "rotationY", -90f, 0f).apply {
            duration = 250
            interpolator = DecelerateInterpolator()
        }

        outAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibleView.visibility = View.GONE
                invisibleView.visibility = View.VISIBLE
                inAnimator.start()
            }
        })

        inAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                holder.isFlipping = false
            }
        })

        outAnimator.start()
        card.isFrontVisible = !card.isFrontVisible
    }
}
