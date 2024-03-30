package com.csci5708.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R
import com.example.dalcommunity.UserMap
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LostAndFoundPostsAdapter(val listener: OnClickListener):
    ListAdapter<UserMap, LostAndFoundPostsAdapter.LostAndFoundViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserMap>() {
            override fun areItemsTheSame(oldItem: UserMap, newItem: UserMap): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UserMap,
                newItem: UserMap
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostAndFoundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_lost_found_item,parent,false)
        return LostAndFoundViewHolder(view)
    }



    fun getTransactionAt(position: Int): UserMap {
        return getItem(position)
    }

    override fun onBindViewHolder(holder: LostAndFoundViewHolder, position: Int) {
        val currentUserMap = getItem(position)
        holder.itemTitleTextView.text = currentUserMap.itemName
        val date = currentUserMap.dateTime
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val parsedDate = LocalDateTime.parse(date, inputFormatter)
        val formattedDate = parsedDate.format(outputFormatter)
        val category = if (currentUserMap.category.equals("lost")) "Lost" else "Found"
        holder.itemDateTextView.text = category + " around "+formattedDate
        Picasso.get().load(currentUserMap.imageUri).into(holder.imageView)
        if(currentUserMap.profileImageUri.isNotEmpty()) {
            Picasso.get().load(currentUserMap.profileImageUri).into(holder.profileImage)
        }
        holder.userNameTextView.setText(currentUserMap.name)
    }

    inner class LostAndFoundViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var itemTitleTextView: TextView =view.findViewById(R.id.itemTitle)
        var itemDateTextView: TextView =view.findViewById(R.id.itemDate)
        var imageView: ImageView = view.findViewById(R.id.itemImage)
        val profileImage:ImageView = view.findViewById(R.id.profileImageView)
        val userNameTextView:TextView = view.findViewById(R.id.userName)

        init {
            view.setOnClickListener {
                if(adapterPosition!= RecyclerView.NO_POSITION)
                    listener.onClickItem(getItem(adapterPosition))
            }
        }
    }
    interface OnClickListener {
        fun onClickItem(userMap: UserMap)
    }
}