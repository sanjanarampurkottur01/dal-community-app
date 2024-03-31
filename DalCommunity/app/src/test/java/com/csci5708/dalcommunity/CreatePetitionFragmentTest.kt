package com.csci5708.dalcommunity

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import com.csci5708.dalcommunity.fragment.petitionsfragments.CreatePetitionFragment
import com.example.dalcommunity.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
//class CreatePetitionFragmentTest {
//
//    private lateinit var createPetitionFragment: CreatePetitionFragment
//    private lateinit var petitionTitleEditText: EditText
//    private lateinit var petitionDescEditText: EditText
//    private lateinit var mockDocumentReference: DocumentReference
//    private lateinit var mockDocumentSnapshot: DocumentSnapshot
//    private lateinit var petitionImage: ImageView
//    private lateinit var scenario: FragmentScenario<CreatePetitionFragment>
//
//
//
//    @Before
//    fun setup() {
//        createPetitionFragment = CreatePetitionFragment()
//        petitionTitleEditText = mock(EditText::class.java)
//        petitionDescEditText = mock(EditText::class.java)
//        createPetitionFragment.petitionTitleEditText = petitionTitleEditText
//        createPetitionFragment.petitionDescEditText = petitionDescEditText
//        mockDocumentReference = mock(DocumentReference::class.java)
//        mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
//        petitionImage = mock(ImageView::class.java)
//        createPetitionFragment.petitionImage = petitionImage
//        val context = mock(Context::class.java)
//        val inflater = mock(LayoutInflater::class.java)
//        val dialog = mock(Dialog::class.java)
//        val view = mock(View::class.java)
//        val imageView = mock(ImageView::class.java)
//        val titleTextView = mock(TextView::class.java)
//        val descTextView = mock(TextView::class.java)
//        val progressBar = mock(RelativeLayout::class.java)
//        val publishButton = mock(Button::class.java)
//        val discardButton = mock(Button::class.java)
//        val provideContextMock: () -> Context = { context }
//
//
//        // Mock behavior of LayoutInflater.inflate
//        `when`(inflater.inflate(R.layout.petition_preview_dialog, null)).thenReturn(view)
//
//        // Mock behavior of View.findViewById
//        `when`(view.findViewById<TextView>(R.id.preview_petition_title)).thenReturn(titleTextView)
//        `when`(view.findViewById<TextView>(R.id.preview_petition_desc)).thenReturn(descTextView)
//        `when`(view.findViewById<ImageView>(R.id.preview_petition_image)).thenReturn(imageView)
//        `when`(view.findViewById<RelativeLayout>(R.id.progressBar)).thenReturn(progressBar)
//
//        // Mock behavior of Dialog
//        `when`(context.resources).thenReturn(ApplicationProvider.getApplicationContext<Context>().resources)
//        `when`(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(inflater)
//        `when`(dialog.window).thenReturn(mock(android.view.Window::class.java))
//
//        // Set up the fragment scenario
//        scenario = launchFragmentInContainer(themeResId = R.style.Theme_DalCommunity) {
//            CreatePetitionFragment().apply {
//                provideContext = provideContextMock
//            }
//        }
//
//
//    }
//
//    @Test
//    fun `test getAllEmails with valid data`() {
//        `when`(mockDocumentReference.get()).thenReturn(getMockTask(mockDocumentSnapshot))
//        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
//        val usersMap = mapOf(
//            "user1@example.com" to true,
//            "user2@example.com" to true,
//            "user3@example.com" to true
//        )
//        `when`(mockDocumentSnapshot.data).thenReturn(mapOf("users" to usersMap))
//
//        val result = runBlocking { createPetitionFragment.getAllEmails(mockDocumentReference) }
//
//        assertEquals(3, result.size)
//        assertEquals(usersMap.keys.toList(), result)
//    }
//
//    @Test
//    fun `test getAllEmails with document not existing`() {
//        `when`(mockDocumentReference.get()).thenReturn(getMockTask(mockDocumentSnapshot))
//        `when`(mockDocumentSnapshot.exists()).thenReturn(false)
//
//        val result = runBlocking { createPetitionFragment.getAllEmails(mockDocumentReference) }
//
//        assertEquals(0, result.size)
//    }
//
//    @Test
//    fun `test getAllEmails with null or empty users map`() {
//        `when`(mockDocumentReference.get()).thenReturn(getMockTask(mockDocumentSnapshot))
//        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
//        `when`(mockDocumentSnapshot.data).thenReturn(mapOf("users" to null))
//
//        val result = runBlocking { createPetitionFragment.getAllEmails(mockDocumentReference) }
//
//        assertEquals(0, result.size)
//    }
//
//    private fun <T> getMockTask(result: T): Task<T> {
//        return Tasks.forResult(result)
//    }
//
//    @Test
//    fun `test isFieldsEdited with both fields empty`() {
//        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionTitleEditText.text.toString()).thenReturn("")
//        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionDescEditText.text.toString()).thenReturn("")
//
//        val result = createPetitionFragment.isFieldsEdited()
//
//        assertEquals(false, result)
//    }
//
//    @Test
//    fun `test isFieldsEdited with petition title empty`() {
//        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionTitleEditText.text.toString()).thenReturn("")
//        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionDescEditText.text.toString()).thenReturn("Description")
//
//        val result = createPetitionFragment.isFieldsEdited()
//
//        assertEquals(true, result)
//    }
//
//    @Test
//    fun `test isFieldsEdited with petition description empty`() {
//        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionTitleEditText.text.toString()).thenReturn("Title")
//        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionDescEditText.text.toString()).thenReturn("")
//
//        val result = createPetitionFragment.isFieldsEdited()
//
//        assertEquals(true, result)
//    }
//
//    @Test
//    fun `test isFieldsEdited with both fields non-empty`() {
//        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionTitleEditText.text.toString()).thenReturn("Title")
//        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
//        `when`(petitionDescEditText.text.toString()).thenReturn("Description")
//
//        val result = createPetitionFragment.isFieldsEdited()
//
//        assertEquals(true, result)
//    }
//    @Test
//    fun testShowPreviewDialog() {
//        scenario.onFragment { fragment ->
//            fragment.showPreviewDialog()
//
//            // Verify the behavior of the function
//            // For example, verify that certain views are set correctly
//            // Verify other expected behavior as needed
//            verify(fragment.view)?.findViewById<TextView>(R.id.preview_petition_title)?.text = "Expected Title"
//            verify(fragment.view)?.findViewById<TextView>(R.id.preview_petition_desc)?.text = "Expected Description"
//            // Verify other assertions or behavior as needed
//        }
//    }
//}
class CreatePetitionFragmentTest {

    private lateinit var createPetitionFragment: CreatePetitionFragment
    private lateinit var petitionTitleEditText: EditText
    private lateinit var petitionDescEditText: EditText
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    private lateinit var petitionImage: ImageView


    @Before
    fun setup() {
        createPetitionFragment = CreatePetitionFragment()
        petitionTitleEditText = mock(EditText::class.java)
        petitionDescEditText = mock(EditText::class.java)
        createPetitionFragment.petitionTitleEditText = petitionTitleEditText
        createPetitionFragment.petitionDescEditText = petitionDescEditText
        mockDocumentReference = mock(DocumentReference::class.java)
        mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

        petitionImage = mock(ImageView::class.java)
        createPetitionFragment.petitionImage = petitionImage

    }

    @Test
    fun `test getAllEmails with valid data`() {
        `when`(mockDocumentReference.get()).thenReturn(getMockTask(mockDocumentSnapshot))
        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        val usersMap = mapOf(
            "user1@example.com" to true,
            "user2@example.com" to true,
            "user3@example.com" to true
        )
        `when`(mockDocumentSnapshot.data).thenReturn(mapOf("users" to usersMap))

        val result = runBlocking { createPetitionFragment.getAllEmails(mockDocumentReference) }

        assertEquals(3, result.size)
        assertEquals(usersMap.keys.toList(), result)
    }

    @Test
    fun `test getAllEmails with document not existing`() {
        `when`(mockDocumentReference.get()).thenReturn(getMockTask(mockDocumentSnapshot))
        `when`(mockDocumentSnapshot.exists()).thenReturn(false)

        val result = runBlocking { createPetitionFragment.getAllEmails(mockDocumentReference) }

        assertEquals(0, result.size)
    }

    @Test
    fun `test getAllEmails with null or empty users map`() {
        `when`(mockDocumentReference.get()).thenReturn(getMockTask(mockDocumentSnapshot))
        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot.data).thenReturn(mapOf("users" to null))

        val result = runBlocking { createPetitionFragment.getAllEmails(mockDocumentReference) }

        assertEquals(0, result.size)
    }

    private fun <T> getMockTask(result: T): Task<T> {
        return Tasks.forResult(result)
    }

    @Test
    fun `test isFieldsEdited with both fields empty`() {
        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionTitleEditText.text.toString()).thenReturn("")
        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionDescEditText.text.toString()).thenReturn("")

        val result = createPetitionFragment.isFieldsEdited()

        assertEquals(false, result)
    }

    @Test
    fun `test isFieldsEdited with petition title empty`() {
        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionTitleEditText.text.toString()).thenReturn("")
        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionDescEditText.text.toString()).thenReturn("Description")

        val result = createPetitionFragment.isFieldsEdited()

        assertEquals(true, result)
    }

    @Test
    fun `test isFieldsEdited with petition description empty`() {
        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionTitleEditText.text.toString()).thenReturn("Title")
        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionDescEditText.text.toString()).thenReturn("")

        val result = createPetitionFragment.isFieldsEdited()

        assertEquals(true, result)
    }

    @Test
    fun `test isFieldsEdited with both fields non-empty`() {
        `when`(petitionTitleEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionTitleEditText.text.toString()).thenReturn("Title")
        `when`(petitionDescEditText.text).thenReturn(mock(Editable::class.java))
        `when`(petitionDescEditText.text.toString()).thenReturn("Description")

        val result = createPetitionFragment.isFieldsEdited()

        assertEquals(true, result)
    }
}
