package com.csci5708.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

/**
 * Adapter for displaying a list of users in a RecyclerView.
 * @param users The list of user data, represented as pairs of (username, userId).
 * @param onItemClick Listener for item click events.
 */
class UsersAdapter(var users: List<User>, private val onItemClick: OnItemClickListener) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    /**
     * Creates a new ViewHolder by inflating the layout for a user item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    /**
     * Interface for handling item click events.
     */
    interface OnItemClickListener {
        fun onItemClick(userName: String, userId: String)
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClick.onItemClick(user.name, user.email)
        }
    }

    /**
     * Returns the total number of items in the data set.
     */
    override fun getItemCount(): Int {
        return users.size
    }

    /**
     * ViewHolder class for representing individual user items.
     */
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.text_view_user_name)
        private val profileImageView: ShapeableImageView = itemView.findViewById(R.id.profileImageViewInPoke)

        /**
         * Binds user data to the views within the ViewHolder.
         */
        fun bind(user:User) {
            userNameTextView.text = user.name
            if (user.photoUri.isNotBlank()) {
                Picasso.get().load(user.photoUri).into(profileImageView)
            }
        }
    }
}
