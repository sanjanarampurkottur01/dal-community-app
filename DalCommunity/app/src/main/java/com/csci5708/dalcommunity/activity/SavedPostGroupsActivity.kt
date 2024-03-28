package com.csci5708.dalcommunity.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.csci5708.dalcommunity.model.ImagePost
import com.example.dalcommunity.R

class SavedPostGroupsActivity : AppCompatActivity(), HomeAdapter.OnImageInItemClickListener {
    private lateinit var savedPosts: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_posts)
        savedPosts = findViewById(R.id.saved_post_list)

        val posts = listOf(ImagePost("", "", 1, "", "", "", "", listOf(""), 0.0, 0.0))
        val adapter = HomeAdapter(this, posts)
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