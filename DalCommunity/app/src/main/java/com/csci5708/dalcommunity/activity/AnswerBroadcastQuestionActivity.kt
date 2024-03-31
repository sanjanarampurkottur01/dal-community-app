package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class AnswerBroadcastQuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_broadcast_question)

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val intent = intent
        val userId = intent.getStringExtra("userEmail")
        val broadcastId = intent.getStringExtra("broadcastId")
        var questionStatement = ""
        val questionStatementTextView = findViewById<TextView>(R.id.questionStatement)
        val yourResponseET = findViewById<EditText>(R.id.questionET)
        val submitBtn = findViewById<TextView>(R.id.answerBtn)
        val loadingPB = findViewById<ProgressBar>(R.id.loadingPB)
        var answers = arrayListOf<String>()
        loadingPB.visibility = View.VISIBLE
        questionStatementTextView.visibility = View.GONE

        val broadcastDocumentRef = Firebase.firestore.collection("broadcastQuestions").document(userId!!)
        broadcastDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data
                if (data != null) {
                    for ((key, value) in data) {
                        if (key.contentEquals(broadcastId)) {
                            val map = value as Map<String, *>
                            questionStatement = map["message"].toString()
                            questionStatementTextView.text = questionStatement
                            loadingPB.visibility = View.GONE
                            questionStatementTextView.visibility = View.VISIBLE
                            if (map.keys.contains("answers")) {
                                answers = map["answers"] as ArrayList<String>
                            }
                        }
                    }
                }
            } else {
                println("Document does not exist")
            }
        }.addOnFailureListener { exception ->
            println("Error getting document: $exception")
        }

        submitBtn.setOnClickListener{
            if (yourResponseET.text.toString().isNotBlank()) {
                answers.add(yourResponseET.text.toString())
                val localMap = mapOf("broadcastId" to broadcastId, "message" to questionStatement, "answers" to answers)
                val map = mapOf(broadcastId to localMap)
                broadcastDocumentRef.set(map, SetOptions.merge())
                Toast.makeText(this, "Your response was submitted.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Enter a response!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}