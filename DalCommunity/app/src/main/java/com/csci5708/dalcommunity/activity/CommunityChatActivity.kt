package com.csci5708.dalcommunity.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.csci5708.dalcommunity.adapter.ChatMessageAdapter
import com.csci5708.dalcommunity.model.ChatMessage
import com.example.dalcommunity.R

class CommunityChatActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSendMessage: Button

    val messages = arrayListOf(
        ChatMessage("Hey, how are you?", "Alice", "abc@gmail.com", 1634607500),
        ChatMessage("I'm doing well, thanks!", "Bob", "sender_1@example.com", 1634607600),
        ChatMessage("What have you been up to lately?", "Alice", "abc@gmail.com", 1634607700),
        ChatMessage("Not much, just working on some projects wjsb hwhjs hjbskhjqwb jhqbwskqjwhb qhjbwskhjwb", "Charlie", "sender_3@example.com", 1634607800),
        ChatMessage("That sounds interesting!", "Diana", "abc@gmail.com", 1634607900),
        ChatMessage("Yeah, it's been keeping me busy.", "Alice", "sender_5@example.com", 1634608000),
        ChatMessage("I can imagine.", "Bob", "abc@gmail.com", 1634608100),
        ChatMessage("Anyway, how about you?", "Alice", "sender_7@example.com", 1634608200),
        ChatMessage("I've been traveling a lot lately.", "Charlie", "abc@gmail.com", 1634608300),
        ChatMessage("That sounds exciting!", "Diana", "sender_9@example.com", 1634608400)
    )

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_community_chat)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val communityTv = findViewById<TextView>(R.id.chatHeadingTv)
        val listView = findViewById<ListView>(R.id.listView)
        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        val sendButton = findViewById<ImageButton>(R.id.sendMessageButton)

        communityTv.text="Technology Geeks"
        listView.adapter=ChatMessageAdapter(messages,this)

        sendButton.setOnClickListener {
            Toast.makeText(this,messageEditText.text,Toast.LENGTH_SHORT).show()
        }
    }
}