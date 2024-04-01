package com.csci5708.dalcommunity
import com.csci5708.dalcommunity.activity.AnnouncementPostActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class AnnouncementPostActivityTest {

    @Test
    fun testActivityNotNull() {
        val activity = Robolectric.buildActivity(AnnouncementPostActivity::class.java).create().get()
        assertNotNull(activity)
    }

    @Test
    fun testAnnouncementPostButtonClicked() {
        val activity = Robolectric.buildActivity(AnnouncementPostActivity::class.java).create().get()
        activity.announcementTitle.setText("Test Title")
        activity.announcementMessage.setText("Test Message")
        activity.postAnnouncementButton.performClick()
        // Assertion for toast message
        assertEquals("Please fill all the fields", ShadowToast.getTextOfLatestToast())
    }

}
