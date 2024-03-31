package com.csci5708.dalcommunity

import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.csci5708.dalcommunity.activity.AccountSettingsActivity
import com.example.dalcommunity.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK])
class AccountSettingsActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(AccountSettingsActivity::class.java)

    @Test
    fun testChangePasswordClick() {
        ActivityScenario.launch(AccountSettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val changePasswdLayout: TextView = activity.findViewById(R.id.acc_sett_change_passwd)
                changePasswdLayout.performClick()
                val startedIntent = Shadows.shadowOf(activity).nextStartedActivity
                assert(startedIntent.component?.className == "com.csci5708.dalcommunity.activity.ChangePasswordActivity")
            }
        }
    }

    @Test
    fun testToolbarName() {
        val title = "Account Settings"
        ActivityScenario.launch(AccountSettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val accountSettingsToolbar: Toolbar = activity.findViewById(R.id.account_settings_toolbar)
                assert(accountSettingsToolbar.title == title)
            }
        }
    }

    @Test
    fun changePasswordDisplay() {
        onView(withId(R.id.acc_sett_change_passwd)).check(
            matches(isDisplayed())
        )
    }
}