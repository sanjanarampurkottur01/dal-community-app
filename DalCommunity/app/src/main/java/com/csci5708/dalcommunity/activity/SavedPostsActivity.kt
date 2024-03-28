package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.Post
import com.example.dalcommunity.R
import com.csci5708.dalcommunity.adapter.SavedGroupListAdapter
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.SavedPost

class SavedPostsActivity : AppCompatActivity(), SavedGroupListAdapter.OnItemClickListener {
    private lateinit var savedPostList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_posts)

        savedPostList = findViewById(R.id.saved_groups)
        val postData = ArrayList<SavedPost>()

        for(i in 0..10) {
            // TODO: Change the URL once firebase has images
            postData.add(SavedPost("Title", "https://cdn.dribbble.com/users/476251/screenshots/2619255/attachments/523315/placeholder.png"))
        }

        val cardAdapter = SavedGroupListAdapter(postData, this)
        cardAdapter.setOnItemClickListener(this)
        savedPostList.layoutManager = GridLayoutManager(this, 2)
        savedPostList.adapter = cardAdapter
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Saved Post clicked", Toast.LENGTH_LONG).show()
    }
}