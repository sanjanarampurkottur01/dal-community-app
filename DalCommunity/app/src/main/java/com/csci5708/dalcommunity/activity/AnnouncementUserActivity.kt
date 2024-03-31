package com.csci5708.dalcommunity.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.AnnouncementAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Announcement
import com.example.dalcommunity.R

class AnnouncementUserActivity : AppCompatActivity() {

    private lateinit var announcementToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var announcementRecyclerView: RecyclerView
    private lateinit var adapter: AnnouncementAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_announcement_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        announcementToolbar = findViewById(R.id.tbAnnouncement)
        announcementRecyclerView = findViewById(R.id.rvAnnouncement)

        setSupportActionBar(announcementToolbar)
        supportActionBar?.title = "Announcements"
        announcementToolbar.setTitleTextColor(Color.WHITE)
        announcementToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.arrow_back_baseline)
        announcementToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        announcementRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AnnouncementAdapter(emptyList())
        announcementRecyclerView.adapter = adapter

        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        // Fetch announcements from Firestore
        FireStoreSingleton.getAllDocumentsOfCollection("announcements",
            onSuccess = { documents ->
                val announcementList = mutableListOf<Announcement>()
                for (document in documents) {
                    // Parse document data and add to announcement list
                    val title = document.getString("title") ?: ""
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getLong("timestamp") ?: 0
                    val senderName = document.getString("senderName") ?: ""
                    announcementList.add(Announcement(title, content, timestamp, senderName))
                }
                // Update RecyclerView with the retrieved announcements
                adapter.updateData(announcementList)
            },
            onFailure = { exception ->
                // Handle failure
            }
        )
    }

    override fun onResume() {
        super.onResume()
        fetchAnnouncements()
    }
}