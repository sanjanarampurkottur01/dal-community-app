package com.csci5708.dalcommunity.tests

import android.app.Application
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.csci5708.dalcommunity.TestApplication
import com.csci5708.dalcommunity.fragment.ProfileFragment
import com.example.dalcommunity.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(androidx.appcompat.R.style.Theme_AppCompat)
        FirebaseApp.initializeApp(this)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class, sdk = [Build.VERSION_CODES.P])
class ProfileFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<ProfileFragment>

    @Before
    fun setUp() {
        // Launch the fragment
        fragmentScenario = launchFragmentInContainer<ProfileFragment>()
        // Move fragment to CREATED state
        fragmentScenario.moveToState(Lifecycle.State.CREATED)
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testFragmentLaunch() {
        // Check if fragment is in CREATED state
        fragmentScenario.onFragment { fragment ->
            Assert.assertEquals(Lifecycle.State.CREATED, fragment.lifecycle.currentState)
        }
    }

    @Test
    fun testFragmentViewVisibility() {
        val latch = CountDownLatch(1)

        fragmentScenario.onFragment { fragment ->
            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    // View is created
                    val view = fragment.requireView()
                    val fabVisibility = view.findViewById<FloatingActionButton>(R.id.create_post_fab).visibility
                    Assert.assertEquals(View.VISIBLE, fabVisibility)
                    latch.countDown() // Release the latch
                }
            }
        }

        // Wait for the fragment's view to be created
        latch.await(2, TimeUnit.SECONDS)
    }

    @Test
    fun testProfileEditButtonVisibility() {
        val latch = CountDownLatch(1)

        fragmentScenario.onFragment { fragment ->
            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    val profileEditButton = fragment.requireView().findViewById<ImageView>(R.id.profile_page_edit_button)
                    Assert.assertEquals(View.VISIBLE, profileEditButton.visibility)
                    latch.countDown() // Release the latch
                }
            }
        }

        // Wait for the fragment's view to be created
        latch.await(2, TimeUnit.SECONDS)
    }

    @Test
    fun testProfileEditButtonClick() {
        val latch = CountDownLatch(1)

        fragmentScenario.onFragment { fragment ->
            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    val profileEditButton = fragment.requireView().findViewById<ImageView>(R.id.profile_page_edit_button)
                    profileEditButton.performClick()
                    val startedIntent = shadowOf(fragment.activity).nextStartedActivity
                    assert(startedIntent.action == Intent.ACTION_PICK)
                    latch.countDown() // Release the latch
                }
            }
        }

        // Wait for the fragment's view to be created
        latch.await(2, TimeUnit.SECONDS)
    }
}