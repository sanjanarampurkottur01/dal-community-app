package com.csci5708.dalcommunity.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R

class SavedPostGroupsActivity : AppCompatActivity() {
    lateinit var savedPosts: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_post_group)

        savedPosts = findViewById(R.id.saved_post_group_list)
        savedPosts.layoutManager = GridLayoutManager(this, 3)
    }
}