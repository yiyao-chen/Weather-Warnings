package com.example.in2000_team41

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.accessibility.AccessibilityChecks
import org.junit.BeforeClass

import org.junit.Rule
import org.junit.Test

class AppStartTest {

    // Sjekker tilgjengelighet automatisk
    companion object {
        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
        }
    }

    // Launch mainactivity
    @get: Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // Sjekker om guiden vises når appen starter opp
    @Test
    fun isGuideInView() {
        // first guide page is displayed
        onView(withId(R.id.guide_1)).check(matches(isDisplayed()))

        // next-button is displayed
        onView(withId(R.id.nextGuide_image)).check(matches(isDisplayed()))

        // go to next page
        onView(withId(R.id.nextGuide_image)).perform(ViewActions.click())

        // second guide page is displayed
        onView(withId(R.id.guide_2)).check(matches(isDisplayed()))

        // next-button is displayed
        onView(withId(R.id.nextGuide_image2)).check(matches(isDisplayed()))

        // go to next page
        onView(withId(R.id.nextGuide_image2)).perform(ViewActions.click())

        // third guide page is displayed
        onView(withId(R.id.guide_3)).check(matches(isDisplayed()))

        // next-button is displayed
        onView(withId(R.id.nextGuide_image3)).check(matches(isDisplayed()))

        // go to next page
        onView(withId(R.id.nextGuide_image3)).perform(ViewActions.click())

        // homeFragment is displayed
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))

    }



    // Sjekker om Home viser det den skal vise
    @Test
    fun isHomeInView() {
        // skip guide
        onView(withId(R.id.skipGuide_tv)).perform(ViewActions.click())

        // homeFragment is displayed
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))

        // user location is displayed
        onView(withId(R.id.address_tv)).check(matches(isDisplayed()))

        // weather is displayed
        onView(withId(R.id.linearlayout_home_weather)).check(matches(isDisplayed()))

        // cardview for MetAlerts is displayed
        onView(withId(R.id.card_weatherAlerts)).check(matches(isDisplayed()))

        // forestfire is displayed
        onView(withId(R.id.linearlayout_home_forstfire)).check(matches(isDisplayed()))

        // cardview for map is displayed
        onView(withId(R.id.card_map)).check(matches(isDisplayed()))
    }

}