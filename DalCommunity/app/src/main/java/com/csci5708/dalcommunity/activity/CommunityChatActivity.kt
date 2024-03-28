package com.csci5708.dalcommunity.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.csci5708.dalcommunity.adapter.ChatMessageAdapter
import com.csci5708.dalcommunity.firestore.FCMNotificationSender
import com.csci5708.dalcommunity.model.ChatMessage
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityChatActivity : AppCompatActivity() {

    private val userEmails = mutableListOf<String>()
    private var fcmTokens = mutableListOf<String>()
    private lateinit var sharedPreferences: SharedPreferences


    /**
     * onCreate is called when the activity is created
     * @param savedInstanceState  The saved state of the activity
     */
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
        val listView = findViewById<ListView>(R.id.listView)
        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        val sendButton = findViewById<ImageButton>(R.id.sendMessageButton)


        val adapter=ChatMessageAdapter(listOf(),this)
        listView.adapter=adapter
        listView.isStackFromBottom=true


        if (groupId != null) {
            fetchChats(adapter,listView,groupId){
                Log.i("MessageList","Message List Updated")
            }
        }

        sendButton.setOnClickListener {
            if(messageEditText.text.isNotEmpty()){
                if (groupId != null) {
                    val currMessage=messageEditText.text.toString()
                    messageEditText.setText("")
                    if (groupName != null) {

                        sharedPreferences = this.getSharedPreferences("CommunityUserData", Context.MODE_PRIVATE)
                        val userName = sharedPreferences.getString("CommunityUserName", "User1") ?: "User2"
                        Log.i("UserDetails","Value from sharedPreference: $userName")
                        sendMessage(groupId,groupName,ChatMessage(currMessage.trim(),userName,currentUser.email!!,System.currentTimeMillis())){

                        }
                    }
                }

            }else{
                Toast.makeText(this,"Message cannot be empty",Toast.LENGTH_SHORT).show()
            }

        }
    }

     /**
     * Sends a chat message to a community group.
     *
     * @param groupId The ID of the community group to send the message to.
     * @param groupname The name of the community group.
     * @param message The chat message to send.
     * @param onComplete A callback function that will be called when the message is sent or an error occurs. It takes a boolean parameter indicating whether the message was sent successfully or not.
     */

    fun sendMessage(groupId: String,groupname:String, message: ChatMessage, onComplete: (success: Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val messagesRef = firestore.collection("community-groups").document(groupId)

        firestore.runTransaction { transaction ->
            val documentSnapshot = transaction.get(messagesRef)
            val currentMessages = documentSnapshot.get("messages") as? List<*> ?: emptyList<HashMap<String, Any>>()
            val usersData = documentSnapshot.get("users") as? HashMap<String, String>

            val userEmails = mutableListOf<String>()
            val emails = usersData?.keys?.toList()
            if (emails != null) {
                userEmails.addAll(emails)
            }



            val updatedMessages = mutableListOf<HashMap<String, Any>>()
            updatedMessages.addAll(currentMessages.map { it as HashMap<String, Any> })

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

            var accessToken = ""
            val SDK_INT = Build.VERSION.SDK_INT
            if (SDK_INT > 8) {
                val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                accessToken = FCMNotificationSender.getAccessToken(this)
            }
            FCMNotificationSender.sendNotificationToMultipleUsers(
                targetTokens = fcmTokens,
                title = "New message in $groupname",
                message = "${message.sentByName} has sent new message!",
                context = this,
                accessToken = accessToken,
                "high"
            )

            true // Return true to commit the transaction
        }.addOnSuccessListener {
            onComplete(true) // Indicate success
        }.addOnFailureListener { exception ->
            onComplete(false) // Indicate failure
        }
    }

    /**
    * Fetches chat messages from Firestore and updates the UI with the new messages.
    *
    * @param adapter The adapter used to display chat messages in the UI.
    * @param listView The ListView widget used to display the chat messages.
    * @param groupId The ID of the community group to fetch chat messages from.
    * @param onComplete A callback function that will be invoked with the fetched chat messages.
    */
    fun fetchChats(adapter: ChatMessageAdapter,listView: ListView, groupId: String, onComplete: (List<ChatMessage>) -> Unit) {
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

                if(fcmTokens.isEmpty()){
                    val usersMap = documentSnapshot.data?.get("users") as? Map<String, Any>
                    if (usersMap != null) {
                        val emails = usersMap.keys.toList()
                        userEmails.clear()
                        userEmails.addAll(emails)
                    }
                    val usersQuery = firestore.collection("users").whereIn("email", userEmails).whereNotEqualTo("email", currentUser.email)

                    usersQuery.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val documents = task.result.documents
                            fcmTokens = documents
                                .mapNotNull { it.getString("fcmToken") }
                                .distinct().toMutableList()
                        } else {
                            Log.w("TAG", "Error getting documents:", task.exception)
                        }
                    }
                }


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

                    onComplete(newMessages)
                    adapter.updateMessages(newMessages)
                    listView.smoothScrollToPosition(newMessages.size-1)
                }
            }
        }

    }

}


