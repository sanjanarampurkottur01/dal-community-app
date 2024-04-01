package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.CommonInterestsUsersAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Activity to display users with common interests
 */
class CommonInterestsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_interests)

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchCurrentUserInterests()
    }

    /**
     * Fetching interests of the current user from Firebase Firestore
     */
    fun fetchCurrentUserInterests() {
        val currentUserEmail = Firebase.auth.currentUser?.email
        if (currentUserEmail != null) {
            FireStoreSingleton.getData(
                "users",
                currentUserEmail,
                onSuccess = { document ->
                    val userInterests = mutableListOf<String>()
                    val firstInterest = document.getString("firstInterest")
                    if (!firstInterest.isNullOrEmpty()) {
                        userInterests.add(firstInterest)
                    }
                    val secondInterest = document.getString("secondInterest")
                    if (!secondInterest.isNullOrEmpty()) {
                        userInterests.add(secondInterest)
                    }
                    val thirdInterest = document.getString("thirdInterest")
                    if (!thirdInterest.isNullOrEmpty()) {
                        userInterests.add(thirdInterest)
                    }
                    fetchAllUsersWithSimilarInterests(userInterests)
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Failed to fetch current user's interests: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    /**
     * Fetches users with similar interests from Firebase Firestore.
     * @param currentUserInterests List of interests of the current user.
     */
    fun fetchAllUsersWithSimilarInterests(currentUserInterests: List<String>) {
        FireStoreSingleton.getAllDocumentsOfCollection(
            "users",
            onSuccess = { documents ->
                val users = mutableListOf<User>()
                val currentUserEmail = Firebase.auth.currentUser?.email
                for (document in documents) {
                    val userEmail = document.getString("email")
                    if (userEmail != null && userEmail != currentUserEmail) {
                        val userName = document.getString("name") ?: "Unknown"
                        val userFirstInterest = document.getString("firstInterest") ?: "None"
                        val userSecondInterest = document.getString("secondInterest") ?: "None"
                        val userThirdInterest = document.getString("thirdInterest") ?: "None"
                        val userPhotoUri = document.getString("photoUri") ?: "Unknown"

                        val userInterests = listOf(userFirstInterest, userSecondInterest, userThirdInterest)

                        if (userInterests.all { it == "None" }) {
                            continue
                        } else if (userInterests.any { currentUserInterests.contains(it) && it != "None" }) {
                            val user = User(userName, userEmail, "", userFirstInterest, userSecondInterest, userThirdInterest, userPhotoUri)
                            users.add(user)
                        }
                    }
                }
                recyclerView.adapter = CommonInterestsUsersAdapter(this, users, currentUserInterests)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Failed to fetch users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}