package com.csci5708.dalcommunity.fragment.CommunityFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.csci5708.dalcommunity.activity.CommunityChatActivity
import com.csci5708.dalcommunity.adapter.CommunityChatsAdapter
import com.csci5708.dalcommunity.model.ChatMessage
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityChatFragment : Fragment() {

    var communityChannels = emptyList<CommunityChannel>()

    private lateinit var adapter: CommunityChatsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_community_chat, container, false)
        val chatsListView=view.findViewById<ListView>(R.id.communityChatLv)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        adapter=CommunityChatsAdapter(emptyList(),requireContext())
        chatsListView.adapter= adapter

        // fetching groups
        fetchGroups(adapter) { groups->
            communityChannels=groups.sortedByDescending { it.lastMessageTime }
        }

        // swiping to refresh
        swipeRefreshLayout.setOnRefreshListener {
            fetchGroups(adapter) { groups->
                communityChannels=groups.sortedByDescending { it.lastMessageTime }
            }
            swipeRefreshLayout.isRefreshing = false
        }

        chatsListView.setOnItemClickListener { parent, view, position, id ->
            val intent=Intent(requireContext(),CommunityChatActivity::class.java)
            intent.putExtra("groupId", communityChannels[position].id )
            intent.putExtra("groupName", communityChannels[position].name )
            startActivity(intent)
        }

        return view
    }

    /**
     * Fetches the community groups from Firestore and updates the provided adapter with the fetched groups.
     *
     * @param adapter The adapter to update with the fetched groups.
     * @param onComplete The callback function to be called when the groups have been fetched and processed.
     *                   The callback function takes a list of [CommunityChannel] as a parameter.
     */
    fun fetchGroups(adapter: CommunityChatsAdapter, onComplete: (List<CommunityChannel>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = Firebase.auth.currentUser!!

        val groupsRef = firestore.collection("community-groups")


        val listenerRegistration = groupsRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.e("Firestore", "Error fetching community groups: $exception")
                return@addSnapshotListener
            }

            var processedGroupCount = querySnapshot?.size() ?: 0

            if (querySnapshot != null) {
                val groupList = mutableListOf<CommunityChannel>()
                for (document in querySnapshot.documents) {
                    val groupId = document.id
                    val userData = document.get("users") as? HashMap<*, *>

                    // Check if users field exists and user email is present as a key
                    if (userData != null && userData.containsKey(currentUser.email!!)) {
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
                    if (processedGroupCount == 0) {
                        onComplete(groupList)
                        adapter.updateCommunities(groupList)
                    }
                }
            }
        }
    }

}