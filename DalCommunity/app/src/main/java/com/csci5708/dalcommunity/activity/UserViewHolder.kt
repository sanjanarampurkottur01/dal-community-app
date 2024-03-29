package com.csci5708.dalcommunity.activity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageViewProfile: ImageView = itemView.findViewById(R.id.imageViewProfile)
    val textViewName: TextView = itemView.findViewById(R.id.textViewName)
    val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)
    val imageViewMessage: ImageView = itemView.findViewById(R.id.imageViewMessage)
    val chipContainer: LinearLayout = itemView.findViewById(R.id.chipContainer)
}
