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
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.ChatMessage
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class CommunityListFragment : Fragment() {

    private var communityChannels= emptyList<CommunityChannel>()
    private var userName:String=""
    private var adapter: CommunityListAdapter? =null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_community_list, container, false)

        val searchBar = view.findViewById<SearchView>(R.id.communitySearchView)
        val communityListView=view.findViewById<ListView>(R.id.communityListLv)
        adapter=CommunityListAdapter(communityChannels,requireContext())
        communityListView.adapter=adapter

        // fetching user data
        FireStoreSingleton.getData(
            "users",
            Firebase.auth.currentUser?.email.toString(),
            { d: DocumentSnapshot -> getUserDataOnSuccess(d) },
            { e -> getUserDataOnFailure() }
        )

        // searching by channel name
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.filter(newText)
                return true
            }
        })

        fetchGroups(adapter!!) { groups->
            communityChannels=groups.sortedByDescending { it.name }
        }

        return view
    }

    /**
     * Retrieves user data in case of failure.
     *
     * @param  paramName    No parameters are required for this function.
     * @return         	   No return value.
     */
    private fun getUserDataOnFailure() {
        Toast.makeText(activity, "Failed to get user data", Toast.LENGTH_LONG).show()
    }

    /**
     * Retrieves user data from a successful document snapshot and updates the user name in the adapter.
     *
     * @param doc The successful document snapshot containing user data.
     */
    private fun getUserDataOnSuccess(doc: DocumentSnapshot) {
        val userDetails = doc.data
        userName=userDetails?.get("name").toString()
        adapter?.updateUserName(userName)
    }

    /**
     * Fetches the community groups from the Firestore database and updates the provided adapter with the fetched data.
     *
     * @param adapter The adapter to update with the fetched community groups.
     * @param onComplete The callback function to be called when the fetching is complete. It takes a list of [CommunityChannel] as a parameter.
     */
    fun fetchGroups(adapter: CommunityListAdapter, onComplete: (List<CommunityChannel>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = Firebase.auth.currentUser!!

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
                    if (processedGroupCount == 0) {
                        onComplete(groupList)
                        adapter.updateCommunities(groupList)
                    }
                }
            }
        }
    }

}