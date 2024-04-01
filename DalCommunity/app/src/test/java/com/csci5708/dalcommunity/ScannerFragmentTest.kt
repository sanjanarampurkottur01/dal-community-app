package com.csci5708.dalcommunity


import android.app.Application
import android.os.Build
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.csci5708.dalcommunity.fragment.ScannerFragment
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


class ScannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(androidx.appcompat.R.style.Theme_AppCompat)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(application = ScannerApplication::class, sdk = [Build.VERSION_CODES.P])
class ScannerFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<ScannerFragment>

    @Before
    fun setUp() {
        fragmentScenario = launchFragmentInContainer<ScannerFragment>()
        fragmentScenario.moveToState(Lifecycle.State.CREATED)
    }

    @Test
    fun testFragmentLaunch() {
        fragmentScenario.onFragment { fragment ->
            Assert.assertEquals(Lifecycle.State.CREATED, fragment.lifecycle.currentState)
        }
    }

}