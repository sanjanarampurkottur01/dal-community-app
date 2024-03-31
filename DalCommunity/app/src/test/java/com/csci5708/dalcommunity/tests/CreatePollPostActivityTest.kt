package com.csci5708.dalcommunity.tests

import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.csci5708.dalcommunity.activity.CreatePollPostActivity
import com.example.dalcommunity.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK])
class CreatePollPostActivityTest {
    @get:Rule
    val activity = activityScenarioRule<CreatePollPostActivity>()

    @Test
    fun testToolbarName() {
        val title = "Create Poll Post"
        ActivityScenario.launch(CreatePollPostActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val toolbar: Toolbar = activity.findViewById(R.id.create_poll_post_toolbar)
                assert(toolbar.title == title)
            }
        }
    }

    @Test
    fun testPollQuestionTv() {
        val pollQuestion = "What is the best pizza place in Halifax?"
        ActivityScenario.launch(CreatePollPostActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val pollQuestionTv: TextView = activity.findViewById(R.id.poll_post_question_input)
                pollQuestionTv.setText(pollQuestion)
                assert(pollQuestionTv.text.toString() == pollQuestion)
            }
        }
    }

    @Test
    fun testPollOption1Visible() {
        onView(withId(R.id.poll_post_option_1)).check(matches(isDisplayed()))
    }

    @Test
    fun testPollOption2Visible() {
        onView(withId(R.id.poll_post_option_2)).check(matches(isDisplayed()))
    }

    @Test
    fun testPollOption3Visible() {
        onView(withId(R.id.poll_post_option_3)).check(matches(isDisplayed()))
    }

    @Test
    fun testPollOption4Visible() {
        onView(withId(R.id.poll_post_option_4)).check(matches(isDisplayed()))
    }
}