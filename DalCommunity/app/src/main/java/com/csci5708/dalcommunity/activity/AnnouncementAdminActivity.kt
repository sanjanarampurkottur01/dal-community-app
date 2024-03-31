package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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

class AnnouncementAdminActivity : AppCompatActivity() {

    private lateinit var adapter: AnnouncementAdapter
    private lateinit var announcementToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var announcementRecyclerView: RecyclerView
    private lateinit var announcementButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_announcement_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        announcementToolbar = findViewById(R.id.tbAnnouncement)
        announcementRecyclerView = findViewById(R.id.rvAnnouncement)
        announcementButton = findViewById(R.id.btnAnnouncement)

        adapter = AnnouncementAdapter(emptyList())
        announcementRecyclerView.layoutManager = LinearLayoutManager(this)
        announcementRecyclerView.adapter = adapter

        setSupportActionBar(announcementToolbar)
        supportActionBar?.title = "Announcements"
        announcementToolbar.setTitleTextColor(Color.WHITE)
        announcementToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.arrow_back_baseline)
        announcementToolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        fetchAnnouncements()

        announcementButton.setOnClickListener{
            val announcementPostActivity = AnnouncementPostActivity()
            startActivity(Intent(this, AnnouncementPostActivity::class.java))
        }
    }

    private fun fetchAnnouncements() {
        // Fetch announcements from Firestore
        FireStoreSingleton.getAllDocumentsOfCollection("announcements",
            onSuccess = { documents ->
                val announcementList = mutableListOf<Announcement>()
                val sortedDocuments = documents.sortedByDescending { document ->
                    document.getLong("timestamp") ?: 0
                }
                for (document in sortedDocuments) {
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