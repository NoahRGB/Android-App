package com.example.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AboutMeActivity : AppCompatActivity() {

    private lateinit var closeNewDeckPopupButton: ImageButton
    private lateinit var deckCountText: TextView
    private lateinit var cardCountText: TextView
    private lateinit var streakText: TextView
    private lateinit var darkModeSwitch: Switch
    private lateinit var deckIcon: ImageView
    private lateinit var cardIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_me_activity)

        closeNewDeckPopupButton = findViewById(R.id.closeAboutMeButton)
        deckCountText = findViewById(R.id.deckCountText)
        cardCountText = findViewById(R.id.cardCountText)
        streakText = findViewById(R.id.streakText)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        deckIcon = findViewById(R.id.deck_icon)
        cardIcon = findViewById(R.id.card_icon)

        // Set icons based on theme
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            deckIcon.setImageResource(R.drawable.deck_icon_dark)
            cardIcon.setImageResource(R.drawable.flashcard_icon_dark)
        } else {
            deckIcon.setImageResource(R.drawable.deck_icon)
            cardIcon.setImageResource(R.drawable.flashcard_icon)
        }

        // return to previous activity if back button is pressed
        closeNewDeckPopupButton.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            // get decks from db to update the deck count
            val db = AppDatabase.getDatabase(this@AboutMeActivity)
            val deckDao = db.deckDao()
            val decksFromDb = deckDao.getAll().first()
            val totalCards = decksFromDb.sumOf { it.cards.size }

            // update the deck count text
            deckCountText.text = getString(R.string.decks_count, decksFromDb.size)
            cardCountText.text = getString(R.string.cards_count, totalCards)
        }

        // --- Streak Logic ---
        val streakPrefs = getSharedPreferences("StreakInfo", Context.MODE_PRIVATE)
        val lastStudiedDateStr = streakPrefs.getString("lastStudiedDate", null)
        var streakCount = streakPrefs.getInt("streakCount", 0)

        if (lastStudiedDateStr != null) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            val today = LocalDate.now()
            val lastStudiedDate = LocalDate.parse(lastStudiedDateStr, formatter)
            val yesterday = today.minusDays(1)

            // if last studied date is not today or yesterday, streak is broken
            if (lastStudiedDate != today && lastStudiedDate != yesterday) {
                streakCount = 0
                streakPrefs.edit().putInt("streakCount", 0).apply()
            }
        }
        streakText.text = getString(R.string.streak_count, streakCount)

        // Load saved theme preference
        val sharedPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getInt(
            "DarkMode",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ) == AppCompatDelegate.MODE_NIGHT_YES
        darkModeSwitch.isChecked = isDarkMode

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefs.edit().putInt("DarkMode", AppCompatDelegate.MODE_NIGHT_YES).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefs.edit().putInt("DarkMode", AppCompatDelegate.MODE_NIGHT_NO).apply()
            }
            recreate()
        }
    }
}