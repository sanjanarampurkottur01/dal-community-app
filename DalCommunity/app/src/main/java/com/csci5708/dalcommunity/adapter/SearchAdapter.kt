package com.csci5708.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.squareup.picasso.Picasso

class SearchAdapter(val listener: OnClickListener):
    ListAdapter<User, SearchAdapter.SearchViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.email == newItem.email
            }

            override fun areContentsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_search, parent, false)
        return SearchViewHolder(view)
    }


    fun getTransactionAt(position: Int): User {
        return getItem(position)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentUser = getItem(position)
        if (currentUser.photoUri.isNotEmpty()) {
            Picasso.get().load(currentUser.photoUri).into(holder.profileImage)
        }
        holder.userNameTextView.setText(currentUser.name)
    }

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profileImageView)
        val userNameTextView: TextView = view.findViewById(R.id.userName)

        init {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    listener.onClickItem(getItem(adapterPosition))
            }
        }
    }

    interface OnClickListener {
        fun onClickItem(userMap: User)
    }
}