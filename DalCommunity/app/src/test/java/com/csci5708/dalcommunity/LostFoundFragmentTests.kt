package com.csci5708.dalcommunity

import android.app.Application
import android.os.Build
import android.view.View
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.csci5708.dalcommunity.fragment.LostFoundFragment
import com.example.dalcommunity.R
import com.google.firebase.FirebaseApp
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LostFoundApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(androidx.appcompat.R.style.Theme_AppCompat)
        FirebaseApp.initializeApp(this)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(application = LostFoundApplication::class, sdk = [Build.VERSION_CODES.P])
class LostFoundFragmentTests {
    private lateinit var fragmentScenario: FragmentScenario<LostFoundFragment>

    @Before
    fun setUp() {
        // Launch the fragment
        fragmentScenario = launchFragmentInContainer<LostFoundFragment>()
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




}