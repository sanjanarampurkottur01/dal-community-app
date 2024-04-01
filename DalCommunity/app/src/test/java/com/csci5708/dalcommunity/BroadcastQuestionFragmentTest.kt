package com.csci5708.dalcommunity

import android.content.SharedPreferences
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.csci5708.dalcommunity.fragment.BroadcastQuestionFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import com.example.dalcommunity.R

class BroadcastQuestionFragmentTest {

    private lateinit var mockSharedPreferences: SharedPreferences

    @Before
    fun setup() {
        mockSharedPreferences = mock(SharedPreferences::class.java)
        `when`(mockSharedPreferences.edit()).thenReturn(mock(SharedPreferences.Editor::class.java))
        `when`(mockSharedPreferences.getString(anyString(), anyString())).thenReturn("0")

        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        `when`(mockFirebaseAuth.currentUser).thenReturn(mock())

        val mockFirebaseFirestore = mock(FirebaseFirestore::class.java)
        `when`(mockFirebaseFirestore.collection(anyString())).thenReturn(mock())

        `when`(ApplicationProvider.getApplicationContext<android.content.Context>().getSharedPreferences(anyString(), anyInt()))
            .thenReturn(mockSharedPreferences)
        `when`(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)
        `when`(FirebaseFirestore.getInstance()).thenReturn(mockFirebaseFirestore)
    }

    @Test
    fun testUIElementsDisplayed() {
        val scenario: FragmentScenario<BroadcastQuestionFragment> = launchFragmentInContainer()
        scenario.onFragment { fragment ->
            onView(withId(R.id.questionET)).check(matches(isDisplayed()))
            onView(withId(R.id.broadcastButton)).check(matches(isDisplayed()))
            onView(withId(R.id.responsesBtn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testBroadcastButtonClick() {
        val scenario: FragmentScenario<BroadcastQuestionFragment> = launchFragmentInContainer()
        scenario.onFragment { fragment ->
            onView(withId(R.id.questionET)).perform(replaceText("Test question"))

            onView(withId(R.id.broadcastButton)).perform(click())

            onView(withId(R.id.broadcastButton)).check(matches(withText("Hold on tight..")))
        }
    }

}