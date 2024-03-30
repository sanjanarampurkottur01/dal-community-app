package com.csci5708.dalcommunity.activity

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.UsersAdapter
import com.csci5708.dalcommunity.firestore.FCMNotificationSender
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity for poking other users.
 * Manages the display of users in a RecyclerView and handles user interactions.
 */
class PokeActivity : AppCompatActivity(), UsersAdapter.OnItemClickListener {
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var originalUsers: List<Pair<String, String>>

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_poke)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_users)
        recyclerView.layoutManager = LinearLayoutManager(this)
        usersAdapter = UsersAdapter(emptyList(), this)
        fetchAllUsers(usersAdapter)
        recyclerView.adapter = usersAdapter
        val searchView: SearchView = findViewById(R.id.search_view)
        searchView.queryHint = "Search by name"
        val editText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(ContextCompat.getColor(this, R.color.text_color))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint))


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterUsers(query, usersAdapter)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    fetchAllUsers(usersAdapter)
                } else {
                    val query = newText.trim()
                    filterUsers(query, usersAdapter)
                }
                return true
            }
        })
    }
    /**
     * Called when a user item is clicked.
     * @param userName The name of the user clicked
     * @param userId The ID of the user clicked
     */

    override fun onItemClick(userName: String, userId: String) {
        showUserInfoDialog(userName, userId)
    }
    /**
     * Displays a dialog showing user information and handles the poke action.
     * @param userName The name of the user to poke
     * @param userEmail The email of the user to poke
     */
    private fun showUserInfoDialog(userName: String, userEmail: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.poke_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        val pokeToUserMessage = dialogView.findViewById<EditText>(R.id.pokeToUserMessage)
        val selectedUserToPoke = dialogView.findViewById<TextView>(R.id.selectedUserToPoke)
        val pokeNowBtn = dialogView.findViewById<AppCompatButton>(R.id.pokeNowBtn)
        selectedUserToPoke.text = "You are Poking To $userName"

        pokeNowBtn.setOnClickListener {
            val message = pokeToUserMessage.text.toString()
            if (message.isNotEmpty()) {
                onPokeClicked(message, userEmail)
                alertDialog.dismiss()
            } else {
                showToast("Please fill the message.")
            }
        }
    }

    /**
     * Handles the poke action when the poke button is clicked.
     * @param message The message to send in the poke
     * @param userEmail The email of the user to poke
     */
    private fun onPokeClicked(message: String, userEmail: String){
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(userEmail)
        var accessToken = ""
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            accessToken = FCMNotificationSender.getAccessToken(this)
        }
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fcmToken = document.getString("fcmToken")
                    val auth = Firebase.auth
                    val currentUser = auth.currentUser
                    if (!fcmToken.isNullOrEmpty() && currentUser != null) {
                        db.collection("users").document(currentUser.email.toString())
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val userName = document.getString("name")
                                    val displayName = userName ?: "Anonymous"
                                    val title = "$displayName Poked You"
                                    val priority = "high"
                                    FCMNotificationSender.sendNotification(
                                        targetToken = fcmToken,
                                        title = title,
                                        message = message,
                                        context = this,
                                        accessToken = accessToken,
                                        priority
                                    )
                                } else {
                                    Log.d(TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(TAG, "get failed with ", exception)
                            }
                    } else {
                        showToast("FCM token not found for user.")
                    }
                } else {
                    showToast("User document not found.")
                }
            }
            .addOnFailureListener { e ->
                showToast("Error fetching user document: $e")
            }
    }
    /**
     * Fetches all users from Firestore and updates the RecyclerView.
     * @param usersAdapter The adapter for the RecyclerView
     */
    private fun fetchAllUsers(usersAdapter: UsersAdapter) {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        FireStoreSingleton.getAllDocumentsOfCollection("users",
            { documents ->
                val users = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    val name = document.getString("name") ?: "Unknown"
                    val id = document.id
                    val email = document.getString("email") ?: "unknown"
                    if (currentUser != null) {
                        if(currentUser.email != email){
                            users.add(Pair(name, email))
                        }
                    }
                }
                originalUsers = users
                usersAdapter.users = users
                usersAdapter.notifyDataSetChanged()
            },
            { exception ->
                Toast.makeText(this, "Failed to load users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
    /**
     * Displays a toast message.
     * @param message The message to display in the toast
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    /**
     * Filters users based on the search query.
     * @param query The search query
     * @param usersAdapter The adapter for the RecyclerView
     */
    private fun filterUsers(query: String, usersAdapter: UsersAdapter) {
        if (query.isBlank()) {
            usersAdapter.users = originalUsers
            usersAdapter.notifyDataSetChanged()
            return
        }

        val filteredUsers = originalUsers.filter { user ->
            user.first.contains(query, ignoreCase = true) // Filter based on name
        }
        usersAdapter.users = filteredUsers
        usersAdapter.notifyDataSetChanged()
    }
}