package com.csci5708.dalcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.R
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import com.csci5708.dalcommunity.fragment.CommentFragment
import com.csci5708.dalcommunity.fragment.LostFoundFragment
import com.csci5708.dalcommunity.fragment.petitionsfragments.TrackPetitionFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LostFoundFragmentTests {
    private lateinit var scenario: FragmentScenario<LostFoundFragment>
    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    private val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)
    private val mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)


    @Before
    fun setUp() {
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseUser.email).thenReturn("test@example.com")
        scenario = launchFragmentInContainer<LostFoundFragment>(Bundle(), androidx.appcompat.R.style.Theme_AppCompat)
        // Wait for fragment to be in a resumed state
        scenario.moveToState(Lifecycle.State.RESUMED)
        val context = ApplicationProvider.getApplicationContext<Context>()
        FirebaseApp.initializeApp(context)
    }
    @Test
    fun testOnCreateView() {
        // Call onCreateView() with mocked parameters

        scenario.onFragment { fragment ->
            val view = fragment.onCreateView(LayoutInflater.from(fragment.context), null, Bundle())
            Assert.assertNotNull(view)
        }
    }
}