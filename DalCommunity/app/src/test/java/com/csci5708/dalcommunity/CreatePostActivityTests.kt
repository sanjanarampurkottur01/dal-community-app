    import android.content.Intent
    import android.graphics.Bitmap
    import android.view.View
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageView
    import androidx.core.widget.doAfterTextChanged
    import androidx.test.core.app.ActivityScenario
    import androidx.test.ext.junit.runners.AndroidJUnit4
    import com.example.dalcommunity.R
    import com.csci5708.dalcommunity.activity.CreatePostActivity
    import org.junit.Test
    import org.junit.runner.RunWith
    import org.robolectric.Shadows.shadowOf
    import org.robolectric.annotation.Config

    @RunWith(AndroidJUnit4::class)
    @Config(sdk = [Config.OLDEST_SDK])
    class CreatePostActivityTest {

        @Test
        fun testGalleryButtonClick() {
            ActivityScenario.launch(CreatePostActivity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    val galleryButton = activity.findViewById<ImageView>(R.id.image_gallery_icon)
                    galleryButton.performClick()
                    val startedIntent = shadowOf(activity).nextStartedActivity
                    assert(startedIntent.action == Intent.ACTION_PICK)
                }
            }
        }

        @Test
        fun testCameraButtonClick() {
            ActivityScenario.launch(CreatePostActivity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    val cameraButton = activity.findViewById<ImageView>(R.id.image_camera_icon)
                    cameraButton.performClick()
                    val startedIntent = shadowOf(activity).nextStartedActivity
                    assert(startedIntent.action == "android.media.action.IMAGE_CAPTURE")
                }
            }
        }

    }