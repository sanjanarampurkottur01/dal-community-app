package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.google.firebase.auth.FirebaseAuth

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var adapter: AnnouncementAdapter
    private lateinit var announcementToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var announcementRecyclerView: RecyclerView
    lateinit var announcementButton: Button

    /**
     * onCreate method to initialize the activity.
     * @param savedInstanceState The saved instance state.
     * @return Unit
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_announcement)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initializing views
        announcementToolbar = findViewById(R.id.tbAnnouncement)
        announcementRecyclerView = findViewById(R.id.rvAnnouncement)
        announcementButton = findViewById(R.id.btnAnnouncement)

        // Initializing adapter
        adapter = AnnouncementAdapter(emptyList())
        announcementRecyclerView.layoutManager = LinearLayoutManager(this)
        announcementRecyclerView.adapter = adapter

        // Setting up toolbar
        setSupportActionBar(announcementToolbar)
        supportActionBar?.title = "Announcements"
        announcementToolbar.setTitleTextColor(Color.WHITE)
        announcementToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.arrow_back_baseline)
        announcementToolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        // Show announcement button only for admin
        if(FirebaseAuth.getInstance().currentUser?.email == "admin@dal.ca"){
            announcementButton.visibility = View.VISIBLE
        } else {
            announcementButton.visibility = View.GONE
        }

        // Fetch announcements
        fetchAnnouncements()

        // Open AnnouncementPostActivity on button click
        announcementButton.setOnClickListener{
            val intent = Intent(this@AnnouncementActivity, AnnouncementPostActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Fetch announcements from Firestore.
     * @return Unit
     */
    fun fetchAnnouncements() {
        // Fetch announcements from FireStore
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
                Log.i("MyTag", "${exception.message}")
            }
        )
    }

    override fun onResume() {
        super.onResume()
        fetchAnnouncements()
    }
}