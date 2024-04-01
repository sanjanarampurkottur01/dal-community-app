package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.example.dalcommunity.R
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.PollPost
import com.csci5708.dalcommunity.model.Post
import com.csci5708.dalcommunity.model.TextPost
import com.google.firebase.firestore.DocumentSnapshot

class SavedPostsActivity : AppCompatActivity(), HomeAdapter.OnImageInItemClickListener {
    private lateinit var savedPostList: RecyclerView

    var actualPostsToDisplay : MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_posts)

        val userPostsToolbar: Toolbar = findViewById(R.id.user_posts_toolbar)
        setSupportActionBar(userPostsToolbar)
        supportActionBar?.title = "My Saved Posts"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        savedPostList = findViewById(R.id.saved_post_list)
        val posts = intent.getStringArrayExtra("posts")

        posts?.forEach {post ->
            FireStoreSingleton.getData("post", post,
                {
                    addDataToList(it)
                    loadDataIntoList()
            }, {})
        }
    }

    private fun loadDataIntoList() {
        val homeAdapter = HomeAdapter(this, actualPostsToDisplay)
        homeAdapter.setOnImageInItemClickListener(this)
        savedPostList.layoutManager = LinearLayoutManager(this)
        savedPostList.adapter = homeAdapter
    }

    private fun addDataToList(document: DocumentSnapshot) {
        if (document.get("type") == 0L) {
            val post = document.toObject(TextPost::class.java)
            actualPostsToDisplay.add(post as TextPost)
        } else if (document.get("type") == 1L) {
            val post = document.toObject(ImagePost::class.java)
            actualPostsToDisplay.add(post as ImagePost)
        } else if (document.get("type") == 2L) {
            val post = document.toObject(PollPost::class.java)
            actualPostsToDisplay.add(post as PollPost)
        }
    }

    override fun onCommentClick(position: Int) {
        Log.e("Test", "test")
    }

    override fun onReportClick(position: Int) {
        Log.e("Test", "test")
    }
}