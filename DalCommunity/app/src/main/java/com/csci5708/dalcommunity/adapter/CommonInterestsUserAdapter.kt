package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csci5708.dalcommunity.activity.ChatActivity
import com.csci5708.dalcommunity.activity.UserViewHolder
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.google.android.material.chip.Chip

class CommonInterestsUsersAdapter(private val context: Context, private val users: List<User>, private val currentUserInterests: List<String>) : RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_common_interests_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        var photo: String
        if (user.photoUri.isNullOrBlank()){
            photo = "https://firebasestorage.googleapis.com/v0/b/dal-community-01.appspot.com/o/profile_image%2Fgoku%40example.com.jpg?alt=media&token=0488377d-08a8-48a4-9acb-1536bf342a64"
        }
        else {
            photo = user.photoUri
        }
        holder.textViewName.text = user.name
        holder.textViewEmail.text = user.email
        Glide.with(holder.itemView.context).load(photo).into(holder.imageViewProfile)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("username", user.name)
            intent.putExtra("email", user.email)
            context.startActivity(intent)
        }

        addInterestChips(holder.chipContainer, user)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    private fun addInterestChips(chipContainer: LinearLayout, user: User) {
        chipContainer.removeAllViews()
        val interests = listOf(user.firstInterest, user.secondInterest, user.thirdInterest)
        interests.forEach { interest ->
            if (currentUserInterests.contains(interest) && interest != "None") {
                val chip = createChip(chipContainer.context, interest)
                chipContainer.addView(chip)
                chipContainer.addView(createSpace(chipContainer.context))
            }
        }
    }

    private fun createChip(context: Context, text: String): Chip {
        val chip = Chip(context)
        chip.text = text
        chip.isClickable = false
        chip.isCheckable = false
        chip.setChipBackgroundColorResource(R.color.background)
        chip.setTextColor(context.resources.getColor(R.color.text_color))
        chip.chipCornerRadius = 30F
        chip.setPadding(16, 8, 16, 8)
        return chip
    }

    private fun createSpace(context: Context): View {
        val space = View(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.width = 16
        space.layoutParams = params
        return space
    }
}