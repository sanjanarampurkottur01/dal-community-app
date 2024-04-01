package com.csci5708.dalcommunity.tests

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import com.csci5708.dalcommunity.activity.AddStudentIDActivity
import com.example.dalcommunity.R
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class AddStudentIDActivityTest {

    private lateinit var activity: AddStudentIDActivity

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        sharedPreferences = Mockito.mock(SharedPreferences::class.java)
        activity = Robolectric.buildActivity(AddStudentIDActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
        activity.sharedPreferences = sharedPreferences
    }

    /**
     * Test if the activity is not null.
     */
    @Test
    fun testActivityNotNull() {
        assertNotNull(activity)
    }

    /**
     * Test if clicking the Capture Photo button starts the image capture intent.
     */
    @Test
    fun testCapturePhotoButtonClicked() {
        val button = activity.findViewById<Button>(R.id.btn_capture_photo)
        button.performClick()
        val expectedIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val actualIntent = Shadows.shadowOf(activity).nextStartedActivity
        assertNotNull(actualIntent)
        assert(actualIntent.component?.className == expectedIntent.component?.className)
    }

    /**
     * Test if clicking the Update Photo button starts the image capture intent.
     */
    @Test
    fun testUpdatePhotoButtonClicked() {
        val button = activity.findViewById<Button>(R.id.btn_update_photo)
        button.performClick()
        val expectedIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val actualIntent = Shadows.shadowOf(activity).nextStartedActivity
        assertNotNull(actualIntent)
        assert(actualIntent.component?.className == expectedIntent.component?.className)
    }

    /**
     * Test if clicking the Delete Photo button removes the image from SharedPreferences.
     */
    @Test
    fun testDeletePhotoButtonClicked() {
        val button = activity.findViewById<Button>(R.id.btn_delete_photo)
        button.performClick()
        verify(sharedPreferences.edit()).remove("student_id_photo")
    }

    /**
     * Test the handleCropResult method to ensure it sets the image view with the cropped image.
     */
    @Test
    fun testHandleCropResult() {
        val uri = Uri.parse("content://media/external/images/media/1")
        val imageView = activity.findViewById<ImageView>(R.id.student_id_photo)
        val method = AddStudentIDActivity::class.java.getDeclaredMethod("handleCropResult", Uri::class.java)
        method.isAccessible = true
        method.invoke(activity, uri)
        assert(imageView.drawable != null)
    }

    /**
     * Test if the appropriate toast message is shown when an image is saved successfully.
     */
    @Test
    fun testToastMessageOnImageSaved() {
        val uri = Uri.parse("content://media/external/images/media/1")
        val method = AddStudentIDActivity::class.java.getDeclaredMethod("handleCropResult", Uri::class.java)
        method.isAccessible = true
        method.invoke(activity, uri)
        val expectedToastMessage = "Image saved successfully"
        val actualToastMessage = ShadowToast.getTextOfLatestToast()
        assert(actualToastMessage == expectedToastMessage)
    }
}
