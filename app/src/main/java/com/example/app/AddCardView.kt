package com.example.app

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd

class AddCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var saveNewCardButton: ImageButton
    private var flipCardButton: ImageButton
    private var flashcardFront: EditText
    private var flashcardBack: EditText
    private var isFlipping = false
    private var isFrontVisible = true

    init {
        LayoutInflater.from(context).inflate(R.layout.add_card_view, this, true)
        flipCardButton = findViewById<ImageButton>(R.id.flipCardButton)
        saveNewCardButton = findViewById<ImageButton>(R.id.saveNewCardButton)
        flashcardFront = findViewById<EditText>(R.id.flashcardFront)
        flashcardBack = findViewById<EditText>(R.id.flashcardBack)

        // sets camera distance so flip is completely visible
        val scale = resources.displayMetrics.density
        flashcardFront.cameraDistance = 8000 * scale
        flashcardBack.cameraDistance = 8000 * scale

        flipCardButton.setOnClickListener {
            if (!isFlipping) {
                flipCard()
            }
        }

        saveNewCardButton.setOnClickListener {
            // add card to database
        }
    }

    private fun flipCard() {
        isFlipping = true

        val outAnim: Int
        val inAnim: Int

        if (isFrontVisible) {
            outAnim = R.animator.card_flip_left_out
            inAnim = R.animator.card_flip_left_in
        } else {
            outAnim = R.animator.card_flip_right_out
            inAnim = R.animator.card_flip_right_in
        }

        val outAnimator = AnimatorInflater.loadAnimator(context, outAnim)
        val inAnimator = AnimatorInflater.loadAnimator(context, inAnim)

        val currentlyVisibleView = if (isFrontVisible) flashcardFront else flashcardBack
        val nextVisibleView = if (isFrontVisible) flashcardBack else flashcardFront

        outAnimator.setTarget(currentlyVisibleView)
        inAnimator.setTarget(nextVisibleView)

        outAnimator.start()
        inAnimator.start()

        outAnimator.doOnEnd {
            currentlyVisibleView.visibility = View.INVISIBLE
            isFlipping = false
        }

        nextVisibleView.visibility = View.VISIBLE

        isFrontVisible = !isFrontVisible
    }
}
