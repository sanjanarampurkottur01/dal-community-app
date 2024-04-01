package com.csci5708.dalcommunity.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.MessageAdapter
import com.csci5708.dalcommunity.firestore.FCMNotificationSender
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Message
import com.example.dalcommunity.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ChatActivity : AppCompatActivity() {

    private lateinit var messageBoxEditText: EditText
    private lateinit var sendMessageImageView: ImageView
    private lateinit var chatToolbar: androidx.appcompat.widget.Toolbar

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    // We create a private room for the sender and receiver pair so that the texts are private and not sent to anyone else
    var senderRoom: String? = null
    var receiverRoom: String? = null

    /**
     * onCreate method to initialize the activity.
     * @param savedInstanceState The saved instance state.
     * @return Unit
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initializing Firestore instance
        val fs = FireStoreSingleton

        // Getting receiver's name and email from intent
        val receiverName = intent.getStringExtra("username")
        val receiverid = intent.getStringExtra("email")

        // Setting up the toolbar
        chatToolbar = findViewById(R.id.tbChat)
        setSupportActionBar(chatToolbar)
        supportActionBar?.setTitle(receiverName)
        chatToolbar.setTitleTextColor(Color.WHITE)
        chatToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.arrow_back_baseline)
        chatToolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        // Getting sender's email
        val senderid = FirebaseAuth.getInstance().currentUser?.email

        // Creating sender and receiver rooms
        senderRoom = receiverid + senderid
        receiverRoom = senderid + receiverid

        // Initializing views
        messageBoxEditText = findViewById(R.id.etMessageBox)
        sendMessageImageView = findViewById(R.id.ivSendMessage)

        // Initializing message list and adapter
        messageList = ArrayList()
        adapter = MessageAdapter(this, messageList)

        // Setting up RecyclerView
        chatRecyclerView = findViewById(R.id.rvChat)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter

        // Fetching chat data from Firestore
        fs.getChatsData(
            "chat",
            senderRoom!!,
            EventListener<QuerySnapshot> { snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed", e)
                    return@EventListener
                }
                if (snapshot != null) {
                    messageList.clear()
                    val sortedDocs = snapshot.documents.sortedBy { it.getLong("sentTime") }
                    for (doc in sortedDocs) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d("TAG", "Current data: null")
                }
            },
            onFailure = { exception ->
                Log.i("MyTag", "${exception.message}")
            }
        )

        // Send message on button click
        sendMessageImageView.setOnClickListener {
            val message = messageBoxEditText.text.toString()
            if (message != ""){
                val messageObject = Message(message, senderid!!, System.currentTimeMillis())

                // Add message to sender room
                FireStoreSingleton.addData(
                    "chat/$senderRoom/messages",
                    messageObject,
                    onComplete = { success ->
                        if (success) {
                            Log.i("MyTag", "Message added successfully")
                        } else {
                            Log.i("MyTag", "Failed to add message")
                        }
                    }
                )

                // Add message to receiver room
                FireStoreSingleton.addData(
                    "chat/$receiverRoom/messages",
                    messageObject,
                    onComplete = { success ->
                        if (success) {
                            Log.i("MyTag", "Message added successfully")
                            val senderName = FirebaseAuth.getInstance().currentUser?.displayName
                            sendNotificationToReceiver(receiverid!!, senderName!!, message)
                        } else {
                            Log.i("MyTag", "Failed to add message")
                        }
                    }
                )

                // Clear the message box
                messageBoxEditText.setText("")
            } else {
                // Notify user that no message has been entered
                Toast.makeText(
                    this@ChatActivity,
                    "Please enter message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    var firestore = FirebaseFirestore.getInstance()
    private fun sendNotificationToReceiver(receiverId: String, senderName: String, message: String) {
        // Fetch receiver's FCM token from Firestore and send notification
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
                if (!senderName.isNullOrEmpty() && !message.isNullOrEmpty()) {
                    // Fetch user document from Firestore
                    firestore.collection("users").document(receiverId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val fcmToken = documentSnapshot.getString("fcmToken")
                            if (!fcmToken.isNullOrEmpty()) {
                                // Send notification
                                FCMNotificationSender.sendNotification(
                                    fcmToken,
                                    senderName,
                                    message,
                                    this,
                                    accessToken,
                                    "high"
                                )
                                Log.i("MyTag", "${senderName}, ${message}, ${receiverId}, ${fcmToken}")
                            } else {
                                Log.i("MyTag", "no token found")
                            }
                        }
                        .addOnFailureListener { exception ->
                           Log.i("MyTag", "${exception.message}")
                        }
                } else {
                    Log.i("ChatActivity", "receiverId, receiverName, or message is null or empty")
                }
            }
            true
        }
    }
}