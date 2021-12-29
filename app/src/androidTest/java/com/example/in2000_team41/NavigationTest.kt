package com.example.in2000_team41

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.in2000_team41.ui.metalerts.MetAlertsAdapter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    // Launch mainactivity
    @get: Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        // skip guide
        onView(withId(R.id.skipGuide_tv)).perform(click())
    }

    // navigate to MapFragment
    // pressback
    @Test
    fun nav_to_map() {
        // navigate to map
        onView(withId(R.id.card_map)).perform(click())
        // MapFragment is displayed
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()))
        // click back-arrow
        pressBack()
        // home is displayed
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))

    }

    // select metAlert-item, navigate to AlertDetailFragment
    // pressback
    @Test
    fun nav_to_alertDetail() {
        // navigate to metAlerts
        onView(withId(R.id.card_weatherAlerts)).perform(click())

        // recyclerview is displayed
        onView(withId(R.id.recyclerview_metalerts)).check(matches(isDisplayed()))

        // click the first element on recyclerview
        onView(withId(R.id.recyclerview_metalerts))
            .perform(actionOnItemAtPosition<MetAlertsAdapter.ViewHolder>(0,click()))

        // navigate to AlertDetailFragment
        onView(withId(R.id.alert_detail_scrollview)).check(matches(isDisplayed()))

        // click back-arrow
        pressBack()

        // recyclerview is displayed
        onView(withId(R.id.recyclerview_metalerts)).check(matches(isDisplayed()))

    }

}