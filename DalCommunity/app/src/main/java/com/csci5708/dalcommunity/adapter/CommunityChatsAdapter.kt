package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R

class CommunityChatsAdapter(private var communities:List<CommunityChannel>, private var context: Context) :
    BaseAdapter() {
    override fun getCount(): Int {
        return communities.size
    }

    override fun getItem(position: Int): Any {
        return communities[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.community_chat_item, parent, false)
        val communityObj=communities[position]

        val initials=getinitials(communityObj.name)

        val communityProfileTv:TextView=view.findViewById(R.id.profile_imageTv)
        val communityNameTv:TextView=view.findViewById(R.id.communityChatNameTv)
        val communityLastMessageTv:TextView=view.findViewById(R.id.communityChatLastTv)


        communityProfileTv.text=initials
        communityNameTv.text=communityObj.name
        if(communityObj.lastMessageSenderName.isNotEmpty() && communityObj.lastMessage.isNotEmpty()){
            communityLastMessageTv.text="${communityObj.lastMessageSenderName}: ${communityObj.lastMessage}"
        }else{
            communityLastMessageTv.text="Start new conversation!"
        }

        return view
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
        this.communities = newCommunities.sortedByDescending { it.lastMessageTime }
        notifyDataSetChanged()
        Log.i("CommunityList","List Updated 2")
        Log.i("CommunityList",newCommunities.toString())
    }
}