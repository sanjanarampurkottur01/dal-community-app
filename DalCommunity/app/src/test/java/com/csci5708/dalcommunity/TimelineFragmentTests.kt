package com.csci5708.dalcommunity

import android.app.Application
import android.os.Build
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.csci5708.dalcommunity.fragment.TimelineFragment
import com.example.dalcommunity.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
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

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [Build.VERSION_CODES.P])
class TimelineFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<TimelineFragment>

    @Before
    fun setUp() {
        // Launch the fragment
        fragmentScenario = launchFragmentInContainer<TimelineFragment>()
        // Move fragment to CREATED state
        fragmentScenario.moveToState(Lifecycle.State.CREATED)
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testFragmentLaunch() {
        // Check if fragment is in CREATED state
        fragmentScenario.onFragment { fragment ->
            assertEquals(Lifecycle.State.CREATED, fragment.lifecycle.currentState)
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
                    assertEquals(View.VISIBLE, fabVisibility)
                    latch.countDown() // Release the latch
                }
            }
        }

        // Wait for the fragment's view to be created
        latch.await(2, TimeUnit.SECONDS)
    }
}
