package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dalcommunity.R

class CreatePollPostActivity : AppCompatActivity() {

    private lateinit var pollQuestionTv: TextView
    private lateinit var pollOption1Tv: TextView
    private lateinit var pollOption2Tv: TextView
    private lateinit var pollOption3Tv: TextView
    private lateinit var pollOption4Tv: TextView
    private lateinit var createPollButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_poll_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val profileDetailToolbar: Toolbar = findViewById(R.id.create_poll_post_toolbar)
        setSupportActionBar(profileDetailToolbar)
        supportActionBar?.title = "Create Poll Post"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        pollQuestionTv = findViewById(R.id.poll_post_question_input)
        pollOption1Tv = findViewById(R.id.poll_post_option_1)
        pollOption2Tv = findViewById(R.id.poll_post_option_2)
        pollOption3Tv = findViewById(R.id.poll_post_option_3)
        pollOption4Tv = findViewById(R.id.poll_post_option_4)
        createPollButton = findViewById(R.id.create_poll_post_button)
        createPollButton.setOnClickListener {  }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}