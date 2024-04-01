package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csci5708.dalcommunity.activity.ChatActivity
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.google.android.material.chip.Chip

/**
 * Adapter for displaying users with common interests.
 * @param context The context in which the adapter is used.
 * @param users List of users with common interests.
 * @param currentUserInterests List of interests of the current user.
 */
class CommonInterestsUsersAdapter(private val context: Context, private val users: List<User>, private val currentUserInterests: List<String>) : RecyclerView.Adapter<CommonInterestsUsersAdapter.UserViewHolder>() {

    /**
     * Create ViewHolder for user items.
     */
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProfile: ImageView = itemView.findViewById(R.id.imageViewProfile) // Profile image
        val textViewName: TextView = itemView.findViewById(R.id.textViewName) // User's name
        val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail) // User's email
        val chipContainer: LinearLayout = itemView.findViewById(R.id.chipContainer) // Container for chips/tags
    }

    /**
     * Create ViewHolder for user items.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_common_interests_user, parent, false)
        return UserViewHolder(view)
    }

    /**
     * Bind ViewHolder with user data.
     */
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

        // Open ChatActivity when user item is clicked
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("username", user.name)
            intent.putExtra("email", user.email)
            context.startActivity(intent)
        }

        // Add interest chips for the user
        addInterestChips(holder.chipContainer, user)
    }

    /**
     * Get total number of users.
     */
    override fun getItemCount(): Int {
        return users.size
    }

    /**
     * Add interest chips for the user.
     */
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

    /**
     * Create a chip for displaying user interests.
     */
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

    /**
     * Create a space between chips.
     */
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
