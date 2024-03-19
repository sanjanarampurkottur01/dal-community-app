package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R

class HomeAdapter(private val context: Context, private val posts: List<String>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

        private var commentClickListener: onCommentClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false)

        return ViewHolder(view, commentClickListener!!)
    }

    override fun getItemCount(): Int {
//        return posts.size
        return 5 // For now, use the line above for the long run
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Logic to set data from the actual post
//        val currentItem = posts[position]
    }

    fun setOnCommentClickListener(listener: onCommentClickListener) {
        commentClickListener = listener
    }

    interface onCommentClickListener {
        fun onCommentClick(position: Int)
    }

    class ViewHolder(itemView: View, private val commentClickListener: onCommentClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private var likeIcon: ImageView
        private var saveIcon: ImageView
        private var commentIcon: ImageView

        private var liked: Boolean = false
        private var saved: Boolean = false

        init {
            likeIcon = itemView.findViewById(R.id.like)
            saveIcon = itemView.findViewById(R.id.save)
            commentIcon = itemView.findViewById(R.id.comment)

            likeIcon.setOnClickListener {
                liked = !liked
                likeIcon.setImageResource(if (liked) R.drawable.like else R.drawable.like_outline)
            }

            saveIcon.setOnClickListener {
                saved = !saved
                saveIcon.setImageResource(if (saved) R.drawable.save else R.drawable.save_outline)
            }

            commentIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                {
                    commentClickListener.onCommentClick(position)
                }
            }
        }
    }
}