package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Post
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SavedPostGroupsActivity : AppCompatActivity(), HomeAdapter.OnImageInItemClickListener {
    private lateinit var savedPosts: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_posts)
        savedPosts = findViewById(R.id.saved_post_list)

        var savedPostsArray: List<Post> = emptyList()

        FireStoreSingleton.getSavedPostsForUser(
            id = Firebase.auth.currentUser?.email.toString(),
            onSuccess = { document ->
                // Convert the DocumentSnapshot to an array of posts
                Log.e("TEST", document.get("value").toString())
                // Now you can use the savedPostsArray variable containing the posts
                // You may want to perform further operations here, such as updating UI
            }
        ) { exception ->
            // Handle the failure case, such as displaying an error message
            Log.e("Firestore", "Failed to fetch saved posts: $exception")
        }

        val adapter = HomeAdapter(this, savedPostsArray)
        adapter.setOnImageInItemClickListener(this)
        savedPosts.adapter = adapter
    }

    override fun onCommentClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onReportClick(position: Int) {
        TODO("Not yet implemented")
    }
}