package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R
import com.csci5708.dalcommunity.adapter.SavedGroupListAdapter
import com.csci5708.dalcommunity.model.SavedPostGroup

class SavedPostsActivity : AppCompatActivity() {
    private lateinit var savedPostList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_post_groups)

        savedPostList = findViewById(R.id.saved_groups)
        val posts = intent.getStringArrayExtra("posts")

        Log.e("Data", posts!![0])
    }
}