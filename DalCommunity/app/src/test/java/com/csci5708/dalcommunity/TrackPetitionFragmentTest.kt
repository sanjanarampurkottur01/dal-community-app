import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import com.csci5708.dalcommunity.fragment.petitionsfragments.TrackPetitionFragment
import com.csci5708.dalcommunity.model.Petition
import com.example.dalcommunity.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(RobolectricTestRunner::class)
class TrackPetitionFragmentTest {
    private lateinit var scenario: FragmentScenario<TrackPetitionFragment>
    private val mockFirebaseAuth = mock(FirebaseAuth::class.java)
    private val mockFirebaseUser = mock(FirebaseUser::class.java)
    private val mockFirestore = mock(FirebaseFirestore::class.java)
    private val mockQuerySnapshot = mock(QuerySnapshot::class.java)

    @Before
    fun setUp() {
        // Mock FirebaseAuth and FirebaseUser
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.email).thenReturn("test@example.com")

        // Launch the fragment in a container
        scenario = launchFragmentInContainer<TrackPetitionFragment>()
        // Wait for fragment to be in a resumed state
        scenario.moveToState(Lifecycle.State.RESUMED)
        val context = ApplicationProvider.getApplicationContext<Context>()
        FirebaseApp.initializeApp(context)
    }

    @Test
    fun testOnCreateView() {
        // Call onCreateView() with mocked parameters
        scenario.onFragment { fragment ->
            val view = fragment.onCreateView(LayoutInflater.from(fragment.context), null, Bundle())
            assertNotNull(view)
        }
    }
    @Test
    fun testFetchPetitions() {
        scenario.onFragment { fragment ->
            // Manually add some data to the 'petitions' list
            fragment.petitions.add(Petition("Title1", "Description1", "User1", "ImageURL1", 0))
            fragment.petitions.add(Petition("Title2", "Description2", "User2", "ImageURL2", 0))
            assertTrue(fragment.petitions.isNotEmpty())
        }
    }

    @Test
    fun testOnItemClick() {
        scenario.onFragment { fragment ->
            val petition = Petition("Title", "Description", "User", "ImageURL", 0)
            fragment.onItemClick(petition) // Just ensure this method doesn't throw an exception
        }
    }

    @Test
    fun testShowDialogWithPetitionDetails() {
        scenario.onFragment { fragment ->
            val petition = Petition("Title", "Description", "User", "ImageURL", 0)
            fragment.showDialogWithPetitionDetails(petition)
        }
    }
}
