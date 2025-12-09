package com.example.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutMeActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AboutMeActivity::class.java)

    private lateinit var db: AppDatabase
    private lateinit var deckDao: DeckDao
    private lateinit var cardDao: CardDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = AppDatabase.getTestDatabase(context)
        deckDao = db.deckDao()
        cardDao = db.cardDao()
        clearDatabase()
    }

    @After
    fun teardown() {
        clearDatabase()
        db.close()
    }

    private fun clearDatabase() = runBlocking {
        deckDao.getAll().first().forEach { deckWithCards ->
            deckDao.delete(deckWithCards.deck)
        }
    }

    @Test
    fun testDeckAndCardCounts() {
        // Add some decks and cards to the database
        runBlocking {
            val deck1 = DeckEntity(deckName = "Deck 1", deckDescription = "")
            val deck2 = DeckEntity(deckName = "Deck 2", deckDescription = "")
            deckDao.insertAll(deck1, deck2)

            val card1 = CardEntity(deckId = 1, frontText = "Front 1", backText = "Back 1")
            val card2 = CardEntity(deckId = 1, frontText = "Front 2", backText = "Back 2")
            val card3 = CardEntity(deckId = 2, frontText = "Front 3", backText = "Back 3")
            cardDao.insertAll(card1, card2, card3)
        }

        // Check that the deck and card counts are displayed correctly
        onView(withId(R.id.deckCountText)).check(matches(withText("Decks: 2")))
        onView(withId(R.id.cardCountText)).check(matches(withText("Cards: 3")))
    }
}
