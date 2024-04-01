package com.csci5708.dalcommunity
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.csci5708.dalcommunity.activity.AnnouncementActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK])
class AnnouncementActivityTest {

    @Test
    fun testActivityNotNull() {
        val activity = Robolectric.buildActivity(AnnouncementActivity::class.java).create().get()
        assertNotNull(activity)
    }

    @Test
    fun testAnnouncementButtonVisibilityForAdmin() {
        val activity = Robolectric.buildActivity(AnnouncementActivity::class.java).create().get()
        activity.fetchAnnouncements() // To simulate successful fetch
        assertEquals(View.VISIBLE, activity.announcementButton.visibility)
    }

}