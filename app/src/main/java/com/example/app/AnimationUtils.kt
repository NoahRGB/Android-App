package com.example.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

object AnimationUtils {

    fun setupCardFlip(view: View) {
        val scale = view.context.resources.displayMetrics.density
        view.cameraDistance = 8000 * scale
    }

    fun flipCard(outView: View, inView: View) {
        val outAnimator = ObjectAnimator.ofFloat(outView, "rotationY", 0f, 90f).apply {
            duration = 250
            interpolator = AccelerateInterpolator()
        }

        val inAnimator = ObjectAnimator.ofFloat(inView, "rotationY", -90f, 0f).apply {
            duration = 250
            interpolator = DecelerateInterpolator()
        }

        outAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                outView.visibility = View.GONE
                inView.visibility = View.VISIBLE
                inAnimator.start()
            }
        })

        outAnimator.start()
    }
}