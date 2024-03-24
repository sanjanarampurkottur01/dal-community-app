package com.csci5708.dalcommunity.fragment.CommunityFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.activity.CommunityChatActivity
import com.csci5708.dalcommunity.adapter.CommunityChatsAdapter
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R

class CommunityChatFragment : Fragment() {

    val communityChannels = arrayListOf(
        CommunityChannel("1", "Technology Enthusiasts", "John Doe", "Welcome to the tech community sjkjshb jhbdkhwjbd ybkwebdkwehjb webdkwjebdkjwhbd hdbwjehbdkwjhbde!", "John Doe"),
        CommunityChannel("2", "Fitness Freaks", "Alice Smith", "Let's get fit together!", "Alice Smith"),
        CommunityChannel("3", "Bookworms Club", "Michael Johnson", "What's everyone reading these days?", "Emily Brown"),
        CommunityChannel("4", "Travel Buddies", "Emma Wilson", "Share your travel stories here!", "David Clark"),
        CommunityChannel("5", "Food Lovers", "Sophia Lee", "Discover and discuss delicious recipes!", "Sophia Lee"),
        CommunityChannel("6", "Photography Enthusiasts", "Ryan Taylor", "Share your best shots!", "Olivia Anderson"),
        CommunityChannel("7", "Artists Hub", "Ethan Martin", "Let's appreciate and critique each other's work.", "Lily Garcia"),
        CommunityChannel("8", "Music Fanatics", "Noah Brown", "What's on your playlist?", "Aiden White"),
        CommunityChannel("9", "Fashion Trends", "Ava Johnson", "Discuss the latest fashion trends!", "Ella Martinez"),
        CommunityChannel("10", "Movie Buffs", "James Wilson", "What's the best movie you've watched recently?", "Liam Thompson"),
    )
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_community_chat, container, false)

        val chatsListView=view.findViewById<ListView>(R.id.communityChatLv)
        chatsListView.adapter=CommunityChatsAdapter(communityChannels,requireContext())

        chatsListView.setOnItemClickListener { parent, view, position, id ->
            val intent=Intent(requireContext(),CommunityChatActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}