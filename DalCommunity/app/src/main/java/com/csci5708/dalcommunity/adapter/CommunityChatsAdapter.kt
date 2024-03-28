package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R

class CommunityChatsAdapter(private var communities:List<CommunityChannel>, private var context: Context) :
    BaseAdapter() {

    /*  
     * Returns the number of items in the list.
     *
     * @return the number of items in the list
     */
    override fun getCount(): Int {
        return communities.size
    }

    /**
     * Returns the item at the specified position in the list.
     *
     * @param position the position of the item in the list
     * @return the item at the specified position
     */
    override fun getItem(position: Int): Any {
        return communities[position]
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
     /**
     * Returns the initials of the given community name.
     *
     * @param  communityName  the name of the community
     * @return the initials of the community name
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
     * Updates the list of communities with the given new communities.
     *
     * @param newCommunities The new list of communities to be updated.
     */
    fun updateCommunities(newCommunities: List<CommunityChannel>) {
        this.communities = newCommunities.sortedByDescending { it.lastMessageTime }
        notifyDataSetChanged()
    }
}