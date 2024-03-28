package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R
import com.csci5708.dalcommunity.adapter.SavedGroupListAdapter
import com.csci5708.dalcommunity.model.SavedPostGroup

class SavedPostsActivity : AppCompatActivity(), SavedGroupListAdapter.OnItemClickListener {
    private lateinit var savedPostList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_post_groups)

        savedPostList = findViewById(R.id.saved_groups)
        val postData = ArrayList<SavedPostGroup>()

        for(i in 0..10) {
            // TODO: Change the URL once firebase has images
            postData.add(SavedPostGroup("Title", "https://cdn.dribbble.com/users/476251/screenshots/2619255/attachments/523315/placeholder.png"))
        }

        val cardAdapter = SavedGroupListAdapter(postData, this)
        cardAdapter.setOnItemClickListener(this)
        savedPostList.layoutManager = GridLayoutManager(this, 2)
        savedPostList.adapter = cardAdapter
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, SavedPostGroupsActivity::class.java)
        intent.putExtra("postGroup", 1)
        startActivity(intent)
    }
}