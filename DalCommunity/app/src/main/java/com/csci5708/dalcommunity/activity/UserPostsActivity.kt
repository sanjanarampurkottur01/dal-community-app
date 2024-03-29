package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.csci5708.dalcommunity.fragment.TimelineFragment
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class UserPostsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_posts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userPostsToolbar: Toolbar = findViewById(R.id.user_posts_toolbar)
        setSupportActionBar(userPostsToolbar)
        supportActionBar?.title = "My Posts"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val timelineFragment =
            TimelineFragment.newInstance(Firebase.auth.currentUser?.email.toString())
        val frTrans = supportFragmentManager.beginTransaction()

        frTrans.replace(
            R.id.user_posts_frame,
            timelineFragment
        ).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}