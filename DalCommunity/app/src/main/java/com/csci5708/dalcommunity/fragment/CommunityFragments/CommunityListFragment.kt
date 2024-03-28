package com.csci5708.dalcommunity.fragment.CommunityFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import com.csci5708.dalcommunity.adapter.CommunityChatsAdapter
import com.csci5708.dalcommunity.adapter.CommunityListAdapter
import com.csci5708.dalcommunity.model.ChatMessage
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class CommunityListFragment : Fragment() {

    var communityChannels= emptyList<CommunityChannel>()


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_community_list, container, false)

        val searchBar = view.findViewById<SearchView>(R.id.communitySearchView)
        val communityListView=view.findViewById<ListView>(R.id.communityListLv)
        val adapter=CommunityListAdapter(communityChannels,requireContext())
        communityListView.adapter=adapter

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.i("SEARCHING","Text: $newText")
                adapter.filter(newText)
                return true
            }
        })

        fetchGroups(adapter) { groups->
            communityChannels=groups.sortedByDescending { it.name }
            Log.i("JoinCommunityList","List Updated")
        }

        return view
    }

    fun fetchGroups(adapter: CommunityListAdapter, onComplete: (List<CommunityChannel>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = Firebase.auth.currentUser!!

        Log.i("JoinCommunityList","function called")

        val groupsRef = firestore.collection("community-groups")


        val listenerRegistration = groupsRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.e("JoinCommunityList", "Error fetching community groups: $exception")
                return@addSnapshotListener
            }

            var processedGroupCount = querySnapshot?.size() ?: 0

            if (querySnapshot != null) {
                val groupList = mutableListOf<CommunityChannel>()
                for (document in querySnapshot.documents) {
                    val groupId = document.id
                    val userData = document.get("users") as? HashMap<*, *>

                    // Check if users field exists and user email is present as a key
                    if (userData != null && !userData.containsKey(currentUser.email!!)) {
                        val communityObj = CommunityChannel(
                            groupId,
                            document.get("name").toString(),
                            document.get("rules").toString(),
                            document.get("desc").toString(),
                            document.get("lastMessage").toString(),
                            document.get("lastMessageSenderEmail").toString(),
                            document.get("lastMessageSenderName").toString(),
                            document.get("lastMessageTime") as Long,
                            document.get("messages") as List<ChatMessage>,
                            document.get("users") as HashMap<String, String>
                        )

                        groupList.add(communityObj)
                    }

                    processedGroupCount--
                    Log.i("JoinCommunityList", "Processed remaining: $processedGroupCount")
                    if (processedGroupCount == 0) {
                        onComplete(groupList)
                        adapter.updateCommunities(groupList)
                    }
                }
            }
        }
    }

}