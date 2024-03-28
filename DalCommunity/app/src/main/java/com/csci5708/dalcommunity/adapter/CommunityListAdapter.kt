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
    override fun getCount(): Int {
        return filteredList.size
    }

    override fun getItem(position: Int): CommunityChannel {
        return filteredList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

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


    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(communities)
        } else {
            filteredList.addAll(communities.filter {
                it.name.lowercase().contains(query.lowercase())
            })
        }
        Log.i("SEARCHING","original this list: $communities")
        Log.i("SEARCHING","Setting this list: $filteredList")
        notifyDataSetChanged()
    }

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

    fun updateCommunities(newCommunities: List<CommunityChannel>) {
        filteredList.clear()
        this.communities=newCommunities
        filteredList.addAll(newCommunities.sortedBy { it.name })
        notifyDataSetChanged()
        Log.i("CommunityList","Join List Updated 2")
        Log.i("CommunityList",newCommunities.toString())
    }

    fun joinCommunity(groupId: String,  onComplete: (success: Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("community-groups").document(groupId)
        val currentUser = Firebase.auth.currentUser!!

        firestore.runTransaction { transaction ->
            val documentSnapshot = transaction.get(groupRef)
            var currentMembers = documentSnapshot.get("users") as? HashMap<String,String> ?:  emptyMap<String,String>()// Handle potential missing field

            val newMembers = HashMap<String, String>(currentMembers) // Create copy
            newMembers.put(currentUser.email!!, currentUser.email!!)


            val updateMap = hashMapOf<String, Any>(
                "users" to newMembers,
            )
            transaction.update(groupRef, updateMap)

            true // Return true to commit the transaction
        }.addOnSuccessListener {
            onComplete(true) // Indicate success
            Log.d("Firestore", "Message added successfully to group: $groupId")
        }.addOnFailureListener { exception ->
            onComplete(false) // Indicate failure
            Log.e("Firestore", "Error adding message to group: $groupId", exception)
        }
    }

}