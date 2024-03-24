package com.csci5708.dalcommunity.fragment.CommunityFragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.csci5708.dalcommunity.adapter.CommunityChatsAdapter
import com.csci5708.dalcommunity.adapter.CommunityListAdapter
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R


class CommunityListFragment : Fragment() {

    val communityChannels= arrayListOf(
        CommunityChannel("11", "Entrepreneurs Network", "Isabella Harris", "Share your startup stories and experiences!", "Lucas Turner"),
        CommunityChannel("12", "Gaming Community", "Mia Clark", "What games are you currently playing?", "Isabella Harris"),
        CommunityChannel("13", "Pet Lovers", "Ella Martinez", "Share pictures and stories of your pets!", "Sophia Lee"),
        CommunityChannel("14", "Language Learners", "Liam Thompson", "Let's learn and practice new languages together!", "Mia Clark"),
        CommunityChannel("15", "DIY Enthusiasts", "Olivia Anderson", "Share your DIY projects and tips!", "Ryan Taylor")
    )


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_community_list, container, false)

        val communityListView=view.findViewById<ListView>(R.id.communityListLv)
        communityListView.adapter= CommunityListAdapter(communityChannels,requireContext())


        return view
    }

}