package com.example.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class DeckListActivity : AppCompatActivity() {

    private lateinit var accountIcon: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // this is the entry point for the app, so apply the current theme
        val sharedPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val nightMode = sharedPrefs.getInt("DarkMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(nightMode)

        setContentView(R.layout.deck_list_activity)

        accountIcon = findViewById(R.id.accountIcon)

        // launch the about me activity
        accountIcon.setOnClickListener {
            val intent = Intent(this, AboutMeActivity::class.java)
            startActivity(intent)
        }

    }
}