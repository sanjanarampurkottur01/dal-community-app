package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R

class CommunityListAdapter (private var communities:ArrayList<CommunityChannel>, private var context: Context) :
    ArrayAdapter<CommunityChannel>(context, R.layout.community_list_item,communities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.community_list_item, parent, false)
        val communityObj=communities[position]

        val communityProfileTv: TextView =view.findViewById(R.id.profile_imageTv)
        val communityNameTv: TextView =view.findViewById(R.id.communityChatNameTv)
        val joinButton:ImageButton=view.findViewById(R.id.communityJoinBt)

        val initials=getinitials(communityObj.communityName)

        communityProfileTv.text=initials
        communityNameTv.text=communityObj.communityName

        joinButton.setOnClickListener{
            Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show()
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
}