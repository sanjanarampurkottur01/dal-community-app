package com.csci5708.dalcommunity.tests

import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.csci5708.dalcommunity.activity.ChangePasswordActivity
import com.example.dalcommunity.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK])
class ChangePasswordActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(ChangePasswordActivity::class.java)

    @Test
    fun testToolbarName() {
        val title = "Change Password"
        ActivityScenario.launch(ChangePasswordActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val toolbar: Toolbar = activity.findViewById(R.id.change_password_toolbar)
                assert(toolbar.title == title)
            }
        }
    }

    @Test
    fun testPasswordInput1Visible() {
        onView(withId(R.id.change_password_input_1)).check(matches(isDisplayed()))
    }

    @Test
    fun testPasswordInput2Visible() {
        onView(withId(R.id.change_password_input_2)).check(matches(isDisplayed()))
    }
}