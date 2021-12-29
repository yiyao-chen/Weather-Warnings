package com.example.in2000_team41

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.android.synthetic.main.fragment_metalerts.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class SearchViewTest {
    // MainActivity is launched each time we run a test
    @get: Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        // skip guide
        onView(withId(R.id.skipGuide_tv)).perform(click())
    }

    // sjekker om søkefeltet kan åpnes og lukkes normalt
    @Test
    fun isSearchViewInView() {
        // navigate to metAlerts
        onView(withId(R.id.card_weatherAlerts)).perform(click())

        // click search icon
        onView(withId(R.id.action_search)).perform(ViewActions.click())

        //input-field should be displayed
        onView(withId(R.id.search_src_text))
            .check(matches(isDisplayed()))

        // type text
        onView(withId(R.id.search_src_text))
            .perform(ViewActions.typeText("tekst"))

        // close-button should be displayed
        onView(withId(R.id.search_close_btn))
            .check(matches(isDisplayed()))

        // close searchview by clicking close-button
        onView(withId(R.id.search_close_btn)).perform(click())
        // click back-arrow
        pressBack()
    }


    // Sjekker om appen viser filtrert liste etter et søk
    // Sjekker om appen viser alle farevarsler når man avslutter et søk
    @Test
    fun navBackAfterSearch() {
        // navigate to metAlerts
        onView(withId(R.id.card_weatherAlerts)).perform(click())

        lateinit var recyclerView: RecyclerView
        activityRule.scenario.onActivity { activity ->
            recyclerView = activity.recyclerview_metalerts
        }

        val listFullSize = recyclerView.adapter?.itemCount
        val input = "o"

        // click search icon
        onView(withId(R.id.action_search)).perform(click())

        // type text
        onView(withId(R.id.search_src_text)).perform(ViewActions.typeText(input))

        val filtertedListSize = recyclerView.adapter?.itemCount

        // close searchview by clicking close-button
        onView(withId(R.id.search_close_btn)).perform(ViewActions.click())

        // filtered list should be displayed
        assertEquals(filtertedListSize, recyclerView.adapter?.itemCount)

        // click back-arrow
        pressBack()

        // original list should be displayed
        assertEquals(listFullSize, recyclerView.adapter?.itemCount)

    }


}