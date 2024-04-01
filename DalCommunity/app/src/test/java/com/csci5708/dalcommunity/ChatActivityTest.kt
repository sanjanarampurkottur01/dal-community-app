package com.csci5708.dalcommunity
import com.csci5708.dalcommunity.activity.ChatActivity
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class ChatActivityTest {

    @Test
    fun testActivityNotNull() {
        val activity = Robolectric.buildActivity(ChatActivity::class.java).create().get()
        assertNotNull(activity)
    }

}
