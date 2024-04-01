package com.csci5708.dalcommunity.activity

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.csci5708.dalcommunity.firestore.FCMNotificationSender
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Announcement
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AnnouncementPostActivity : AppCompatActivity() {

    lateinit var announcementTitle: EditText
    lateinit var announcementMessage: EditText
    lateinit var postAnnouncementButton: Button

    /**
     * onCreate method to initialize the activity.
     * @param savedInstanceState The saved instance state.
     * @return Unit
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_announcement_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initializing views
        announcementTitle = findViewById(R.id.etAnnouncementTitle)
        announcementMessage = findViewById(R.id.etAnnouncementMessage)
        postAnnouncementButton = findViewById(R.id.btnPostAnnouncement)

        // Set OnClickListener for postAnnouncementButton
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

    /**
     * Add announcement to Firestore.
     * @param announcement The announcement to be added.
     * @return Unit
     */
    private fun addAnnouncementToDatabase(announcement: Announcement) {
        // Add announcement to FireStore
        FireStoreSingleton.addData("announcements", announcement) { isSuccess ->
            if (isSuccess) {
                val title = announcement.title ?: "Announcement"
                val content = announcement.content ?: "by admin"
                // Send notification to everyone
                sendAnnouncementNotificationToEveryone(title, content)
                Toast.makeText(this, "Announcement posted successfully!", Toast.LENGTH_SHORT).show()
                // Finish this activity
                finish()
            } else {
                Toast.makeText(this, "Failed to post announcement", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var firestore = FirebaseFirestore.getInstance()
    /**
     * Send announcement notification to all users.
     * @param title The title of the announcement.
     * @param content The content of the announcement.
     * @return Unit
     */
    private fun sendAnnouncementNotificationToEveryone(title: String?, content: String?) {
        firestore.runTransaction { _ ->

            var accessToken = ""
            val SDK_INT = Build.VERSION.SDK_INT
            if (SDK_INT > 8) {
                val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                accessToken = FCMNotificationSender.getAccessToken(this)
            }
            this.let {
                if (!title.isNullOrEmpty() && !content.isNullOrEmpty()) {
                    FireStoreSingleton.getAllDocumentsOfCollection("users",
                        onSuccess = { documents ->
                            val tokens = mutableListOf<String>()
                            for (document in documents) {
                                val fcmToken = document.getString("fcmToken")
                                if (!fcmToken.isNullOrEmpty()) {
                                    tokens.add(fcmToken)
                                }
                            }

                            FCMNotificationSender.sendNotificationToMultipleUsers(
                                targetTokens = tokens,
                                title = title,
                                message = content,
                                context = this,
                                accessToken = accessToken,
                                priority = "high"
                            )
                            Log.i("MyTag", "${tokens.size}")
                        },
                        onFailure = { exception ->
                            // Handle failure to fetch FCM tokens
                            exception.printStackTrace()
                        }
                    )
                } else {
                    Log.i("MyTag", "title or content null")
                }
            }
            true
        }
    }
}