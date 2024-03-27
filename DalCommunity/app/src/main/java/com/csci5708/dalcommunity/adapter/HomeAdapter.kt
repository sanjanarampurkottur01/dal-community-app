package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.PollPost
import com.csci5708.dalcommunity.model.Post
import com.csci5708.dalcommunity.model.TextPost
import com.example.dalcommunity.R

class HomeAdapter(private val context: Context, private val posts: List<Post>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var imageInItemClickListener: OnImageInItemClickListener? = null

    override fun getItemViewType(position: Int): Int {
        return posts[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            0 -> {
                val view = inflater.inflate(R.layout.item_text_post_layout, parent, false)
                TextViewHolder(view, imageInItemClickListener!!)
            }
            1 -> {
                val view = inflater.inflate(R.layout.item_image_post_layout, parent, false)
                ImageViewHolder(view, imageInItemClickListener!!)
            }
            2 -> {
                val view = inflater.inflate(R.layout.item_poll_posts_layout, parent, false)
                PollViewHolder(view, imageInItemClickListener!!)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currPost = posts[position]

        when (holder) {
            is ImageViewHolder ->
                if (currPost is ImagePost) {
                    holder.bind(context, currPost)
                }
            is PollViewHolder ->
                if (currPost is PollPost) {
                    holder.bind(context, currPost)
                }
            is TextViewHolder ->
                if (currPost is TextPost) {
                    holder.bind(context, currPost)
                }
        }
    }

    fun setOnImageInItemClickListener(listener: OnImageInItemClickListener) {
        imageInItemClickListener = listener
    }

    interface OnImageInItemClickListener {
        fun onCommentClick(position: Int)
        fun onReportClick(position: Int)
    }

    class ImageViewHolder(itemView: View, private val imageInItemClickListener: OnImageInItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private var likeIcon: ImageView
        private var saveIcon: ImageView
        private var commentIcon: ImageView
        private var reportIcon: ImageView

        private var postCaption: TextView
        private var postTime: TextView
        private var userName: TextView
        private var userImage: ImageView
        private var locationTag: TextView

        private var liked: Boolean = false
        private var saved: Boolean = false

        init {
            likeIcon = itemView.findViewById(R.id.like)
            saveIcon = itemView.findViewById(R.id.save)
            commentIcon = itemView.findViewById(R.id.comment)
            reportIcon = itemView.findViewById(R.id.report)
            locationTag = itemView.findViewById(R.id.location_post)

            postCaption = itemView.findViewById(R.id.text_post)
            postTime = itemView.findViewById(R.id.data_time)
            userName = itemView.findViewById(R.id.user)
            userImage = itemView.findViewById(R.id.user_icon)

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
                if (position != RecyclerView.NO_POSITION) {
                    imageInItemClickListener.onCommentClick(position)
                }
            }

            reportIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    imageInItemClickListener.onReportClick(position)
                }
            }
        }

        fun bind(context: Context, post: ImagePost) {
            postCaption.text = post.caption
            postTime.text = post.time
            userName.text = post.userName
            locationTag.text = "${post.latLocation}, ${post.longLocation}"
            Log.e("TEST", "IMAGE")
        }
    }

    class PollViewHolder(itemView: View, private val imageInItemClickListener: OnImageInItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private var likeIcon: ImageView
        private var saveIcon: ImageView
        private var commentIcon: ImageView
        private var reportIcon: ImageView

        private var postTime: TextView
        private var userName: TextView
        private var userImage: ImageView

        private var liked: Boolean = false
        private var saved: Boolean = false
        private val pollRecyclerView: RecyclerView
        private lateinit var pollAdapter: PollAdapter
        private val pollQuestion: TextView

        init {
            likeIcon = itemView.findViewById(R.id.like)
            saveIcon = itemView.findViewById(R.id.save)
            commentIcon = itemView.findViewById(R.id.comment)
            reportIcon = itemView.findViewById(R.id.report)

            postTime = itemView.findViewById(R.id.data_time)
            userName = itemView.findViewById(R.id.user)
            userImage = itemView.findViewById(R.id.user_icon)

            pollRecyclerView = itemView.findViewById(R.id.poll_recycler_view)
            pollQuestion = itemView.findViewById(R.id.poll_question_text)

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
                if (position != RecyclerView.NO_POSITION) {
                    imageInItemClickListener.onCommentClick(position)
                }
            }

            reportIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    imageInItemClickListener.onReportClick(position)
                }
            }
        }

        fun bind(context: Context, post: PollPost) {
            postTime.text = post.time
            userName.text = post.userName
            post.calculatePercentages()
            pollAdapter = PollAdapter(context, post)
            pollQuestion.text = post.pollQuestion
            pollRecyclerView.layoutManager = LinearLayoutManager(context)
            pollRecyclerView.adapter = pollAdapter
        }
    }

    class TextViewHolder(itemView: View, private val imageInItemClickListener: OnImageInItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private var likeIcon: ImageView
        private var saveIcon: ImageView
        private var commentIcon: ImageView
        private var reportIcon: ImageView
        private var locationTag: TextView

        private var postCaption: TextView
        private var postTime: TextView
        private var userName: TextView
        private var userImage: ImageView

        private var liked: Boolean = false
        private var saved: Boolean = false

        init {
            likeIcon = itemView.findViewById(R.id.like)
            saveIcon = itemView.findViewById(R.id.save)
            commentIcon = itemView.findViewById(R.id.comment)
            reportIcon = itemView.findViewById(R.id.report)
            locationTag = itemView.findViewById(R.id.location_post)

            postCaption = itemView.findViewById(R.id.text_post)
            postTime = itemView.findViewById(R.id.data_time)
            userName = itemView.findViewById(R.id.user)
            userImage = itemView.findViewById(R.id.user_icon)

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
                if (position != RecyclerView.NO_POSITION) {
                    imageInItemClickListener.onCommentClick(position)
                }
            }

            reportIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    imageInItemClickListener.onReportClick(position)
                }
            }
        }

        fun bind(context: Context, post: TextPost) {
            postCaption.text = post.caption
            postTime.text = post.time
            userName.text = post.userName
            locationTag.text = "${post.latLocation}, ${post.longLocation}"
            Log.e("TEST", "TEXT")
        }
    }
}