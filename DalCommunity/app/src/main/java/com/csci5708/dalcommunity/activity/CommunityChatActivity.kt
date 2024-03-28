package com.csci5708.dalcommunity.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.csci5708.dalcommunity.adapter.ChatMessageAdapter
import com.csci5708.dalcommunity.model.ChatMessage
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityChatActivity : AppCompatActivity() {


    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_community_chat)

        val groupId = intent.getStringExtra("groupId")
        val groupName = intent.getStringExtra("groupName")

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.title=groupName
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = groupName
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_baseline)
        }

        toolbar.setNavigationOnClickListener { finish() }


        val currentUser = Firebase.auth.currentUser!!
//        val communityTv = findViewById<TextView>(R.id.chatHeadingTv)
        val listView = findViewById<ListView>(R.id.listView)
        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        val sendButton = findViewById<ImageButton>(R.id.sendMessageButton)



//        communityTv.text=groupName
        val adapter=ChatMessageAdapter(listOf(),this)
        listView.adapter=adapter


        if (groupId != null) {
            fetchChats(adapter,groupId){
                Log.i("MessageList","Message List Updated")
            }
        }

        sendButton.setOnClickListener {
            if(messageEditText.text.isNotEmpty()){
                if (groupId != null) {
                    val currMessage=messageEditText.text.toString()
                    messageEditText.setText("")
                    sendMessage(groupId,ChatMessage(currMessage.trim(),currentUser.email!!,currentUser.email!!,System.currentTimeMillis())){
                    }
                }

            }else{
                Toast.makeText(this,"Message cannot be empty",Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun sendMessage(groupId: String, message: ChatMessage, onComplete: (success: Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val messagesRef = firestore.collection("community-groups").document(groupId)

        firestore.runTransaction { transaction ->
            val documentSnapshot = transaction.get(messagesRef)
            val currentMessages = documentSnapshot.get("messages") as? List<*> ?: emptyList<HashMap<String, Any>>() // Handle potential missing field

            val updatedMessages = mutableListOf<HashMap<String, Any>>()
            updatedMessages.addAll(currentMessages.map { it as HashMap<String, Any> }) // Convert to mutable list

            val messageMap:HashMap<String, Any> = hashMapOf(
                "message" to message.message,
                "sentByName" to message.sentByName,
                "sentById" to message.sentById,
                "messageTime" to message.messageTime
            )
            updatedMessages.add(messageMap)

            val updateMap = hashMapOf<String, Any>(
                "messages" to updatedMessages,
                "lastMessage" to message.message,
                "lastMessageSenderEmail" to message.sentById,
                "lastMessageSenderName" to message.sentByName,
                "lastMessageTime" to System.currentTimeMillis()
            )
            transaction.update(messagesRef, updateMap)

            true // Return true to commit the transaction
        }.addOnSuccessListener {
            onComplete(true) // Indicate success
            Log.d("Firestore", "Message added successfully to group: $groupId")
        }.addOnFailureListener { exception ->
            onComplete(false) // Indicate failure
            Log.e("Firestore", "Error adding message to group: $groupId", exception)
        }
    }

    fun fetchChats(adapter: ChatMessageAdapter, groupId: String, onComplete: (List<ChatMessage>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = Firebase.auth.currentUser!!

        val messagesRef = firestore.collection("community-groups").document(groupId)


        messagesRef.addSnapshotListener { documentSnapshot, exception ->
            if (exception != null) {
                Log.e("Firestore", "Error fetching updated messages: $exception")
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val newMessagesData = documentSnapshot.get("messages") as? List<*>

                if (newMessagesData != null) {
                    val newMessages = mutableListOf<ChatMessage>()
                    for (messageMap in newMessagesData) {
                        val messageObj = ChatMessage(
                            (messageMap as HashMap<*, *>?)?.get("message") as String,
                            (messageMap as HashMap<*, *>?)?.get("sentByName") as String,
                            (messageMap as HashMap<*, *>?)?.get("sentById") as String,
                            (messageMap as HashMap<*, *>?)?.get("messageTime") as Long
                        )
                        newMessages.add(messageObj)
                    }


                    // Update your adapter with the new messages
                    onComplete(newMessages)
                    adapter.updateMessages(newMessages)
                }
            }
        }

    }

    override fun onBackPressed() {
        // Handle back button press here
        // You can finish the activity, show a dialog, etc.
        super.onBackPressed() // Call super to navigate back to parent (optional)
    }
}


