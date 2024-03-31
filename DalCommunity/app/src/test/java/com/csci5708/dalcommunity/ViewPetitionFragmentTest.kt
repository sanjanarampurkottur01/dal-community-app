import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.csci5708.dalcommunity.fragment.petitionsfragments.ViewPetitionFragment
import com.csci5708.dalcommunity.model.Petition
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import com.example.dalcommunity.R

@RunWith(RobolectricTestRunner::class)
class ViewPetitionFragmentTest {

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var fragmentManager: FragmentManager
    private lateinit var scenario: FragmentScenario<ViewPetitionFragment>


    @Before
    fun setup() {
        fragmentActivity = mock(FragmentActivity::class.java)
        fragmentManager = mock(FragmentManager::class.java)

        // Mocking FragmentManager behavior
        `when`(fragmentActivity.supportFragmentManager).thenReturn(fragmentManager)
        // Create a mock context
        val context = mock(Context::class.java)

        // Create a mock provideContext lambda
        val provideContextMock: () -> Context = { context }

        // Create a mock inflater
        var inflater = mock(LayoutInflater::class.java)

        // Mock behavior of findViewById calls
        val dialogView = mock(View::class.java)
        val titleOfPetition = mock(TextView::class.java)
        val descriptionOfPetition = mock(TextView::class.java)
        val numberOfPetition = mock(TextView::class.java)
        val imageOfPetition = mock(ImageView::class.java)
        val close = mock(RelativeLayout::class.java)
        val checkboxSignIn = mock(CheckBox::class.java)
        val signInPetitionBtn = mock(Button::class.java)

        `when`(inflater.inflate(R.layout.view_petition, null)).thenReturn(dialogView)
        `when`(dialogView.findViewById<TextView>(R.id.viewPetitionTitle)).thenReturn(titleOfPetition)
        `when`(dialogView.findViewById<TextView>(R.id.viewDescriptionPetition)).thenReturn(descriptionOfPetition)
        `when`(dialogView.findViewById<TextView>(R.id.viewPetitionSignedNumber)).thenReturn(numberOfPetition)
        `when`(dialogView.findViewById<ImageView>(R.id.viewImagePetition)).thenReturn(imageOfPetition)
        `when`(dialogView.findViewById<RelativeLayout>(R.id.close_icon)).thenReturn(close)
        `when`(dialogView.findViewById<CheckBox>(R.id.petitionSignIn)).thenReturn(checkboxSignIn)
        `when`(dialogView.findViewById<Button>(R.id.signInPetitionBtn)).thenReturn(signInPetitionBtn)

        scenario = launchFragmentInContainer(themeResId = R.style.Theme_DalCommunity) {
            ViewPetitionFragment().apply {
                provideContext = provideContextMock
            }
        }
    }

    @Test
    fun testShowDialogWithPetitionDetails() {
        scenario.onFragment { fragment ->
            val petition = Petition("1", "Title", "Description", "0", 0)
            fragment.showDialogWithPetitionDetails(petition)
            assertNotNull(fragment.getContextOfView())
        }
    }
}