package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.fragment.CommentFragment
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.example.dalcommunity.R

class HomeActivity : AppCompatActivity(), HomeAdapter.onCommentClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val posts = listOf("", "", "")

        var adapter = HomeAdapter(this, posts)
        adapter.setOnCommentClickListener(this)
        recyclerView.adapter = adapter
    }

    override fun onCommentClick(position: Int) {
        Toast.makeText(this,"test",Toast.LENGTH_LONG).show()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_top_comment, R.anim.slide_out_down_comment)
        fragmentTransaction.replace(R.id.fragment_container, CommentFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}