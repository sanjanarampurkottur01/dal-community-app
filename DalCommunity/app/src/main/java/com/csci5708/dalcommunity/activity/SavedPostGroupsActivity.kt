package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.csci5708.dalcommunity.adapter.SavedGroupListAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Post
import com.csci5708.dalcommunity.model.SavedPostGroup
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SavedPostGroupsActivity : AppCompatActivity(), SavedGroupListAdapter.OnItemClickListener {
    private lateinit var savedPosts: RecyclerView
    private lateinit var data: List<SavedPostGroup>

    override fun onResume() {
        super.onResume()
        FireStoreSingleton.getSavedPostGroupsForUser(
            id = Firebase.auth.currentUser?.email.toString(),
            onSuccess = { document ->
                data = document
                setRecyclerViewData(data)
            }
        ) { exception ->
            // Handle the failure case, such as displaying an error message
            Log.e("FireStore", "Failed to fetch saved posts: $exception")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_post_groups)
        savedPosts = findViewById(R.id.saved_groups)
    }

    private fun setRecyclerViewData(data: List<SavedPostGroup>) {
        val adapter = SavedGroupListAdapter(this, data)
        savedPosts.layoutManager = GridLayoutManager(this, 2)
        savedPosts.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        Log.e("Test", "test")
        val intent = Intent(this, SavedPostsActivity::class.java)
        intent.putExtra("posts", data[position].posts)
        startActivity(intent)
    }

}