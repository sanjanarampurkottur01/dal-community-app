package com.csci5708.dalcommunity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.CommentAdapter
import com.csci5708.dalcommunity.fragment.CommentFragment
import com.example.dalcommunity.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CommentFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<CommentFragment>

    @Before
    fun setUp() {
        fragmentScenario = launchFragmentInContainer<CommentFragment>(Bundle(), androidx.appcompat.R.style.Theme_AppCompat)
    }

    @Test
    fun testFragmentNotNull() {
        fragmentScenario.onFragment { fragment ->
            assertEquals(View.VISIBLE, fragment.view?.visibility)
        }
    }

    @Test
    fun testRecyclerViewNotNull() {
        fragmentScenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerViewComments)
            assertEquals(View.VISIBLE, recyclerView.visibility)
        }
    }

    @Test
    fun testInitialEditTextVisibility() {
        fragmentScenario.onFragment { fragment ->
            val editText = fragment.requireView().findViewById<EditText>(R.id.comment_text_box)
            assertEquals(View.VISIBLE, editText.visibility)
        }
    }

    @Test
    fun testInitialButtonVisibility() {
        fragmentScenario.onFragment { fragment ->
            val button = fragment.requireView().findViewById<Button>(R.id.button_id)
            assertEquals(View.GONE, button.visibility)
        }
    }

    @Test
    fun testAddTextWatcher() {
        fragmentScenario.onFragment { fragment ->
            val editText = fragment.requireView().findViewById<EditText>(R.id.comment_text_box)
            val button = fragment.requireView().findViewById<Button>(R.id.button_id)

            editText.setText("Test")

            assertEquals(View.VISIBLE, button.visibility)
        }
    }

    @Test
    fun testRemoveTextWatcher() {
        fragmentScenario.onFragment { fragment ->
            val editText = fragment.requireView().findViewById<EditText>(R.id.comment_text_box)
            val button = fragment.requireView().findViewById<Button>(R.id.button_id)

            editText.setText("Test")
            editText.setText("")

            assertEquals(View.GONE, button.visibility)
        }
    }

    @Test
    fun testRecyclerViewAdapter() {
        fragmentScenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerViewComments)
            val adapter = recyclerView.adapter

            assertEquals(true, adapter is CommentAdapter)
        }
    }

    @Test
    fun testCommentAdapterItemCount() {
        fragmentScenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerViewComments)
            val adapter = recyclerView.adapter as CommentAdapter

            assertEquals(2, adapter.itemCount)
        }
    }

    @Test
    fun testFragmentAnimation() {
        fragmentScenario.onFragment { fragment ->
            assertEquals(true, fragment.isResumed)
        }
    }

    @Test
    fun testButtonClickListener() {
        fragmentScenario.onFragment { fragment ->
            val button = fragment.requireView().findViewById<Button>(R.id.button_id)
            button.performClick()
            // Add assertion for button click action
        }
    }

    @Test
    fun testEditTextInput() {
        fragmentScenario.onFragment { fragment ->
            val editText = fragment.requireView().findViewById<EditText>(R.id.comment_text_box)
            editText.setText("Test input")
            assertEquals("Test input", editText.text.toString())
        }
    }
}
