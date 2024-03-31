package com.csci5708.dalcommunity.tests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.csci5708.dalcommunity.fragment.ReportFragment
import com.csci5708.dalcommunity.fragment.ReportSuccessFragment
import com.example.dalcommunity.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK])
class ReportFragmentTests {

    @Mock
    lateinit var mockLayoutInflater: LayoutInflater

    @Mock
    lateinit var mockViewGroup: ViewGroup

    @Mock
    lateinit var mockBundle: Bundle

    @Mock
    lateinit var mockRecyclerView: RecyclerView

    @Mock
    lateinit var mockFragmentManager: FragmentManager

    lateinit var reportFragment: ReportFragment

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        reportFragment = ReportFragment()
    }

    @Test
    fun testOnCreateView() {
        val mockView = mock(android.view.View::class.java)
        `when`(
            mockLayoutInflater.inflate(
                R.layout.fragment_report_post,
                mockViewGroup,
                false
            )
        ).thenReturn(mockView)

        val view = reportFragment.onCreateView(mockLayoutInflater, mockViewGroup, mockBundle)

        assertEquals(mockView, view)
    }
}
