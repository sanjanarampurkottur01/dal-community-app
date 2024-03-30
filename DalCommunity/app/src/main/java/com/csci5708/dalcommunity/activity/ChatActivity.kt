package com.csci5708.dalcommunity.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.MessageAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Message
import com.example.dalcommunity.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val fs = FireStoreSingleton

        val receiverName = intent.getStringExtra("username")
        val receiverid = intent.getStringExtra("email")

        chatToolbar = findViewById(R.id.tbChat)
        setSupportActionBar(chatToolbar)
        supportActionBar?.setTitle(receiverName)
        chatToolbar.setTitleTextColor(Color.WHITE)
        chatToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.arrow_back_baseline)
        chatToolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        val senderid = FirebaseAuth.getInstance().currentUser?.email

        senderRoom = receiverid + senderid
        receiverRoom = senderid + receiverid

        messageBoxEditText = findViewById(R.id.etMessageBox)
        sendMessageImageView = findViewById(R.id.ivSendMessage)

        messageList = ArrayList()
        adapter = MessageAdapter(this, messageList)

        chatRecyclerView = findViewById(R.id.rvChat)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter

        fs.getDataRealTime(
            "chat",
            senderRoom!!,
            EventListener<QuerySnapshot> { snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed", e)
                    return@EventListener
                }
                if (snapshot != null) {
                    messageList.clear()
                    var sortedDocs = snapshot.documents.sortedBy { it.getLong("sentTime") }
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
                // Handle failure
            }
        )

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
                           // Message added successfully
                       } else {
                           // Failed to add message
                       }
                   }
               )

               // Add message to receiver room
               FireStoreSingleton.addData(
                   "chat/$receiverRoom/messages",
                   messageObject,
                   onComplete = { success ->
                       if (success) {
                           // Message added successfully
                       } else {
                           // Failed to add message
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
}