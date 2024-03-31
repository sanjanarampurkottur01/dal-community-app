package com.csci5708.dalcommunity

import android.view.View
import com.csci5708.dalcommunity.adapter.UsersAdapter
import com.csci5708.dalcommunity.model.User
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.ArgumentCaptor


class UsersAdapterTest {

    @Mock
    private lateinit var mockItemClickListener: UsersAdapter.OnItemClickListener
    @Mock
    private lateinit var mockView: View


    private lateinit var usersAdapter: UsersAdapter
    @Mock
    private lateinit var mockViewHolder: UsersAdapter.UserViewHolder
    val argumentCaptor = ArgumentCaptor.forClass(User::class.java)


    @Before
    fun setUp() {
        initMocks(this)
        usersAdapter = UsersAdapter(emptyList(), mockItemClickListener)
    }

    @Test
    fun `getItemCount should return correct count`() {
        val users = listOf(
            User("John", "john@example.com", "desc1", "interest1", "interest2", "interest3", "photo_uri_1"),
            User("Alice", "alice@example.com", "desc2", "interest1", "interest2", "interest3", "photo_uri_2")
        )
        usersAdapter.users = users
        assertEquals(users.size, usersAdapter.itemCount)
    }
}