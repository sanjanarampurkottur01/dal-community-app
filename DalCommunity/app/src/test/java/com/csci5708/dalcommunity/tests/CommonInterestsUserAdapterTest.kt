package com.csci5708.dalcommunity.tests

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.CommonInterestsUsersAdapter
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class CommonInterestsUsersAdapterTest {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        `when`(mockContext.resources).thenReturn(mockResources)
    }

    private val mockResources: android.content.res.Resources = mock(android.content.res.Resources::class.java)

    /**
     * Test getItemCount() method of CommonInterestsUsersAdapter.
     */
    @Test
    fun testGetItemCount() {
        val users = listOf(User("Yola", "yola@example.com", "", "Programming", "Hiking", "", ""))
        val adapter = CommonInterestsUsersAdapter(mockContext, users, listOf("Programming", "Hiking"))
        assertEquals(1, adapter.itemCount)
    }

    /**
     * Test onBindViewHolder() method of CommonInterestsUsersAdapter.
     */
    @Test
    fun testOnBindViewHolder() {
        val users = listOf(User("Yola", "yola@example.com", "", "Programming", "Hiking", "", ""))
        val adapter = CommonInterestsUsersAdapter(mockContext, users, listOf("Programming", "Hiking"))
        val viewHolder = adapter.onCreateViewHolder(mock(RecyclerView::class.java), 0)
        adapter.onBindViewHolder(viewHolder, 0)

        val nameTextView: TextView = viewHolder.itemView.findViewById(R.id.textViewName)
        val emailTextView: TextView = viewHolder.itemView.findViewById(R.id.textViewEmail)

        assertEquals("Yola", nameTextView.text)
        assertEquals("yola@example.com", emailTextView.text)
    }

    /**
     * Test chip creation in CommonInterestsUsersAdapter.
     */
    @Test
    fun testChipCreation() {
        val users = listOf(User("Yola", "yola@example.com", "", "Programming", "Hiking", "", ""))
        val adapter = CommonInterestsUsersAdapter(mockContext, users, listOf("Programming", "Hiking"))
        val viewHolder = adapter.onCreateViewHolder(mock(RecyclerView::class.java), 0)
        adapter.onBindViewHolder(viewHolder, 0)

        val chipContainer: View = viewHolder.itemView.findViewById(R.id.chipContainer)
        val chipCount = (chipContainer as ViewGroup).childCount

        assertEquals(2, chipCount)
        verify(mockContext.resources).getColor(R.color.text_color)
    }
}
