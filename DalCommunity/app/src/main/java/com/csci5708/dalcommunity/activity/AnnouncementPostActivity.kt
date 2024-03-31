package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Announcement
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.Calendar

class AnnouncementPostActivity : AppCompatActivity() {

    private lateinit var announcementTitle: EditText
    private lateinit var announcementMessage: EditText
    private lateinit var postAnnouncementButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_announcement_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        announcementTitle = findViewById(R.id.etAnnouncementTitle)
        announcementMessage = findViewById(R.id.etAnnouncementMessage)
        postAnnouncementButton = findViewById(R.id.btnPostAnnouncement)

        postAnnouncementButton.setOnClickListener {
            val title = announcementTitle.text.toString().trim()
            val message = announcementMessage.text.toString().trim()

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val timestamp = Calendar.getInstance().timeInMillis
                val senderName = Firebase.auth.currentUser?.displayName
                val announcement = Announcement(title, message, timestamp, senderName)
                addAnnouncementToDatabase(announcement)
            }
        }
    }

    private fun addAnnouncementToDatabase(announcement: Announcement) {
        // Add announcement to Firestore
        FireStoreSingleton.addData("announcements", announcement,
            onComplete = { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(this, "Announcement posted successfully!", Toast.LENGTH_SHORT).show()

                    // Finish this activity
                    finish()
                } else {
                    Toast.makeText(this, "Failed to post announcement", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}