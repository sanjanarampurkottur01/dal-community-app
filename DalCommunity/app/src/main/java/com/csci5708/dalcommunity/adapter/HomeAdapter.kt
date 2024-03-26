package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.Post
import com.example.dalcommunity.R

class HomeAdapter(private val context: Context, private val posts: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var imageInItemClickListener: OnImageInItemClickListener? = null

    override fun getItemViewType(position: Int): Int {
        val currObject = posts[position]

        // if (currObject.type.equals("post")) {
        if (currObject == "post") {
            return 0
        } else {
            return 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context);
        return if (viewType == 0) {
            val view = inflater.inflate(R.layout.home_item, parent, false)
            PostViewHolder(view, imageInItemClickListener!!)
        } else {
            val view = inflater.inflate(R.layout.home_item, parent, false)
            PollViewHolder(view, imageInItemClickListener!!)
        }
    }

    override fun getItemCount(): Int {
//        return posts.size
        return 5 // For now, use the line above for the long run
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currPost = posts[position]

        when (holder) {
            is PostViewHolder -> holder.bind(currPost)
            is PollViewHolder -> holder.bind(currPost)
        }
        // Logic to set data from the actual post
//        val currentItem = posts[position]
    }

    fun setOnImageInItemClickListener(listener: OnImageInItemClickListener) {
        imageInItemClickListener = listener
    }

    interface OnImageInItemClickListener {
        fun onCommentClick(position: Int)
        fun onReportClick(position: Int)
    }

    class PostViewHolder(itemView: View, private val imageInItemClickListener: OnImageInItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private var likeIcon: ImageView
        private var saveIcon: ImageView
        private var commentIcon: ImageView
        private var reportIcon: ImageView

        private var liked: Boolean = false
        private var saved: Boolean = false

        init {
            likeIcon = itemView.findViewById(R.id.like)
            saveIcon = itemView.findViewById(R.id.save)
            commentIcon = itemView.findViewById(R.id.comment)
            reportIcon = itemView.findViewById(R.id.report)

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
                    imageInItemClickListener.onCommentClick(position)
                }
            }

            reportIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                {
                    imageInItemClickListener.onReportClick(position)
                }
            }
        }

//        fun bind(post: Post) {
        fun bind(post: String) {
            Log.e("TEST", "POST")
        }
    }

    class PollViewHolder(itemView: View, private val imageInItemClickListener: OnImageInItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private var likeIcon: ImageView
        private var saveIcon: ImageView
        private var commentIcon: ImageView
        private var reportIcon: ImageView

        private var liked: Boolean = false
        private var saved: Boolean = false

        init {
            likeIcon = itemView.findViewById(R.id.like)
            saveIcon = itemView.findViewById(R.id.save)
            commentIcon = itemView.findViewById(R.id.comment)
            reportIcon = itemView.findViewById(R.id.report)

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
                    imageInItemClickListener.onCommentClick(position)
                }
            }

            reportIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                {
                    imageInItemClickListener.onReportClick(position)
                }
            }
        }

//        fun bind(post: Post) {
        fun bind(post: String) {
            Log.e("TEST", "POLL")
        }
    }
}