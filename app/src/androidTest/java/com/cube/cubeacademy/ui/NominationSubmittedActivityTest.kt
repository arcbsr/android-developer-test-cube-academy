package com.cube.cubeacademy.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.cube.cubeacademy.R
import com.cube.cubeacademy.lib.di.AppModule
import com.cube.cubeacademy.presentation.ui.activities.NominationSubmittedActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class) // Optional: Uninstall modules if needed for testing purposes
class NominationSubmittedActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(NominationSubmittedActivity::class.java)

    // Inject dependencies for the test on setup
    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // Check if MainActivity is opened by verifying a view element in MainActivity
    @Test
    fun clickButton_opensCreateNominationActivity() {
        onView(withId(R.id.submit_button)).perform(click())
        onView(withId(R.id.back_button)).check(matches(isDisplayed()))
    }

}