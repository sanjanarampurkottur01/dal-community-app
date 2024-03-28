package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.csci5708.dalcommunity.model.ChatMessage
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityListAdapter (private var communities:List<CommunityChannel>, private var context: Context) :
    BaseAdapter() {

    private var filteredList = communities.toMutableList()
    private var userName:String=""

    /**
     * Returns the number of items in the list.
     *
     * @return the number of items in the list
     */
    override fun getCount(): Int {
        return filteredList.size
    }

    /**
     * Returns the item at the specified position in the list.
     *
     * @param position the position of the item in the list
     * @return the item at the specified position
     */
    override fun getItem(position: Int): CommunityChannel {
        return filteredList[position]
    }
    /** 
     * Returns the ID of the item at the specified position in the list.
     *
     * @param position the position of the item in the list
     * @return the ID of the item at the specified position
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * Retrieves a view for the specified position in the list.
     *
     * @param position the position of the item in the list
     * @param convertView the view to be reused if possible, or null if not
     * @param parent the parent ViewGroup that the view will be attached to
     * @return the View to be displayed at the specified position
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.community_list_item, parent, false)
        val communityObj=filteredList[position]

        val communityProfileTv: TextView =view.findViewById(R.id.profile_imageTv)
        val communityNameTv: TextView =view.findViewById(R.id.communityChatNameTv)
        val joinButton:ImageButton=view.findViewById(R.id.communityJoinBt)
        val communityCount: TextView =view.findViewById(R.id.communityMemberCount)

        val initials=getinitials(communityObj.name)

        communityProfileTv.text=initials
        communityNameTv.text=communityObj.name
        communityCount.text="Member count: ${communityObj.users.size}"

        joinButton.setOnClickListener{
            joinCommunity(communityObj.id){
                Toast.makeText(context,"Successfully joined Community!",Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    /**
     * Filters the list of communities based on the given query.
     *
     * @param query The query to filter the list of communities by.
     */
    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(communities)
        } else {
            filteredList.addAll(communities.filter {
                it.name.lowercase().contains(query.lowercase())
            })
        }
        notifyDataSetChanged()
    }

    /**
     * Returns the initials of a given community name.
     *
     * @param communityName The name of the community.
     * @return The initials of the community name.
     */
    private fun getinitials(communityName:String):String{
        val names=communityName.split(" ")

        if(names.size>1){
            return names[0].substring(0,1).uppercase()+names[1].substring(0,1).uppercase()
        }else{
            if(names[0].length>1){
                return names[0].substring(0,2).uppercase()
            }else{
                return "CC"
            }
        }
    }

    /**
     * Updates the list of communities with the provided new communities.
     *
     * @param newCommunities The new list of communities to update with.
     */
    fun updateCommunities(newCommunities: List<CommunityChannel>) {
        filteredList.clear()
        this.communities=newCommunities
        filteredList.addAll(newCommunities.sortedBy { it.name })
        notifyDataSetChanged()
    }

    // Updates the user's name with the given userName.
    fun updateUserName(userName:String){
        this.userName=userName
    }

    /**
     * Joins a community group by updating the group's user list with the current user's information.
     *
     * @param groupId The ID of the group to join.
     * @param onComplete A callback function that will be called with a boolean indicating whether the join operation was successful or not.
     */
    private fun joinCommunity(groupId: String, onComplete: (success: Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("community-groups").document(groupId)
        val currentUser = Firebase.auth.currentUser!!

        firestore.runTransaction { transaction ->
            val documentSnapshot = transaction.get(groupRef)
            var currentMembers = documentSnapshot.get("users") as? HashMap<String,String> ?:  emptyMap<String,String>()

            val newMembers = HashMap<String, String>(currentMembers) // Create copy
            newMembers[currentUser.email!!] = userName


            val updateMap = hashMapOf<String, Any>(
                "users" to newMembers,
            )
            transaction.update(groupRef, updateMap)

            true // Return true to commit the transaction
        }.addOnSuccessListener {
            onComplete(true) // Indicate success

        }.addOnFailureListener { exception ->
            onComplete(false) // Indicate failure
            Log.e("Firestore", "Error adding message to group: $groupId", exception)
        }
    }

}