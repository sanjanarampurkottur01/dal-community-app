package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.PollPost
import com.csci5708.dalcommunity.model.PollValue
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
        createPollButton.setOnClickListener {
            val pollQuestion: String = pollQuestionTv.text.toString()
            val pollValues: ArrayList<PollValue> = ArrayList()

            if (pollQuestion.isNotEmpty()) {
                if (pollOption1Tv.text.toString().isNotEmpty()) {
                    pollValues.add(
                        PollValue(
                            pollOption1Tv.text.toString(),
                            0,
                            false
                        )
                    )
                }

                if (pollOption2Tv.text.toString().isNotEmpty()) {
                    pollValues.add(
                        PollValue(
                            pollOption2Tv.text.toString(),
                            0,
                            false
                        )
                    )
                }

                if (pollOption3Tv.text.toString().isNotEmpty()) {
                    pollValues.add(
                        PollValue(
                            pollOption3Tv.text.toString(),
                            0,
                            false
                        )
                    )
                }

                if (pollOption4Tv.text.toString().isNotEmpty()) {
                    pollValues.add(
                        PollValue(
                            pollOption4Tv.text.toString(),
                            0,
                            false
                        )
                    )
                }

                if (pollValues.size >= 2) {
                    val pollPost: PollPost = PollPost(
                        "",
                        Firebase.auth.currentUser?.email.toString(),
                        2,
                        pollQuestion,
                        pollValues,
                        false,
                        Firebase.auth.currentUser?.displayName.toString()
                    )

                    FireStoreSingleton.addData(
                        "post",
                        pollPost
                    ) { b: Boolean -> uploadPollPostOnComplete(b) }
                } else {
                    Toast.makeText(this, "Please provide more than two options!", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please enter your poll question.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadPollPostOnComplete(success: Boolean) {
        if (success) {
            Toast.makeText(this, "Successfully created poll post!", Toast.LENGTH_LONG).show()
            val homeActivityIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeActivityIntent)
            finish()
        } else
            Toast.makeText(this, "Failed to create poll post. Please try again.", Toast.LENGTH_LONG)
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}