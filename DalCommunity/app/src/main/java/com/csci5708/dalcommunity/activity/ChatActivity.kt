package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.MessageAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Message
import com.example.dalcommunity.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var messageBoxEditText: EditText
    private lateinit var sendMessageImageView: ImageView

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

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("email")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title =name

        messageBoxEditText = findViewById(R.id.etMessageBox)
        sendMessageImageView = findViewById(R.id.ivSendMessage)

        messageList = ArrayList()
        adapter = MessageAdapter(this, messageList)

        chatRecyclerView = findViewById(R.id.rvChat)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter

        fs.getDataRealTime("chat", senderRoom!!, EventListener<DocumentSnapshot> {snapshot, e ->
            if(e!= null){
                Log.w("TAG", "Listen failed", e)
                return@EventListener
            }
            if(snapshot != null && snapshot.exists()){
                messageList.clear()
                val messages = snapshot.get("message") as? List<HashMap<String, Any>>

                // Convert message data to Message objects and add them to messageList
                messages?.forEach { messageData ->
                    val message = Message(
                        messageData["message"] as String?,
                        messageData["senderId"] as String?
                    )
                    messageList.add(message)
                }

                // Notify adapter of data change
                adapter.notifyDataSetChanged()
            } else {
                Log.d("TAG", "Current data: null")
            }
        }, onFailure = {exception ->

        })

        sendMessageImageView.setOnClickListener {
            val message = messageBoxEditText.text.toString()
            val messageObject = Message(message, senderUid)

            // Add message to sender room
            fs.addData(
                "chat",
                senderRoom!!,
                mapOf("message" to messageObject), // Data to add
                onComplete = { success ->
                    if (success) {
                        // Message added to sender room successfully
                    } else {
                        // Failed to add message to sender room
                    }
                }
            )

            // Add message to receiver room
            fs.addData(
                "chat",
                receiverRoom!!,
                mapOf("message" to messageObject), // Data to add
                onComplete = { success ->
                    if (success) {
                        // Message added to receiver room successfully
                    } else {
                        // Failed to add message to receiver room
                    }
                }
            )

            // Clear the message box
            messageBoxEditText.setText("")
        }

    }
}