package com.csci5708.dalcommunity.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.PollPostsAdapter
import com.csci5708.dalcommunity.model.Collections
import com.example.dalcommunity.R

class TempActivity : AppCompatActivity() {
    lateinit var pollPostsRecyclerView: RecyclerView
    lateinit var pollPostsAdapter: PollPostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_temp)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_temp)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pollPostsRecyclerView = findViewById(R.id.poll_posts_recycler_view)
        setPollPostsRecyclerView()
    }

    private fun setPollPostsRecyclerView() {
        pollPostsRecyclerView.layoutManager = LinearLayoutManager(this)
        pollPostsAdapter = PollPostsAdapter(this, Collections.getPollPosts())
        pollPostsRecyclerView.adapter = pollPostsAdapter
    }
}