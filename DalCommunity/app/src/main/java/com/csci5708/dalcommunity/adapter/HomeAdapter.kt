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
import com.bumptech.glide.Glide
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.PollPost
import com.csci5708.dalcommunity.model.Post
import com.csci5708.dalcommunity.model.TextPost
import com.example.dalcommunity.R

/**
 * Adapter for the home timeline RecyclerView.
 *
 * @property context The context of the activity or fragment.
 * @property posts The list of posts to be displayed.
 * @constructor Initializes the HomeAdapter with the provided context and list of posts.
 */
class HomeAdapter(private val context: Context, private val posts: List<Post>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var imageInItemClickListener: OnImageInItemClickListener? = null

    /**
     * Gets the view type of the item at the given position in the RecyclerView.
     *
     * @param position The position of the item in the RecyclerView.
     * @return An integer representing the type of the view.
     */
    override fun getItemViewType(position: Int): Int {
        return posts[position].type
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            0 -> {
                val view = inflater.inflate(R.layout.item_text_post_layout, parent, false)
                Log.e("TEST", "HERE")
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return posts.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
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

    /**
     * Sets the listener for image item click events.
     *
     * @param listener The listener to be set.
     */
    fun setOnImageInItemClickListener(listener: OnImageInItemClickListener) {
        imageInItemClickListener = listener
    }

    /**
     * Interface definition for a callback to be invoked when an image item is clicked.
     */
    interface OnImageInItemClickListener {
        /**
         * Called when a comment icon is clicked.
         *
         * @param position The position of the item in the RecyclerView.
         */
        fun onCommentClick(position: Int)
        /**
         * Called when a report icon is clicked.
         *
         * @param position The position of the item in the RecyclerView.
         */
        fun onReportClick(position: Int)
    }

    /**
     * ViewHolder for image posts.
     *
     * @property itemView The view corresponding to the item layout.
     * @property imageInItemClickListener The listener for image item click events.
     * @constructor Initializes the ImageViewHolder with the given itemView and listener.
     */
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
        private var postImage: ImageView

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
            postImage = itemView.findViewById(R.id.image_post)

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

            Glide.with(context)
                .load(post.imageUrl)
                .into(postImage)
        }
    }

    /**
     * ViewHolder for poll posts.
     *
     * @property itemView The view corresponding to the item layout.
     * @property imageInItemClickListener The listener for image item click events.
     * @constructor Initializes the PollViewHolder with the given itemView and listener.
     */
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

    /**
     * ViewHolder for text posts.
     *
     * @property itemView The view corresponding to the item layout.
     * @property imageInItemClickListener The listener for image item click events.
     * @constructor Initializes the TextViewHolder with the given itemView and listener.
     */
    class TextViewHolder(itemView: View, private val imageInItemClickListener: OnImageInItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        // ImageView for liking the post
        private var likeIcon: ImageView
        // ImageView for saving the post
        private var saveIcon: ImageView
        // ImageView for commenting on the post
        private var commentIcon: ImageView
        // ImageView for reporting the post
        private var reportIcon: ImageView
        // TextView for displaying the location tag
        private var locationTag: TextView

        // TextView for displaying the post caption
        private var postCaption: TextView
        // TextView for displaying the post time
        private var postTime: TextView
        // TextView for displaying the username
        private var userName: TextView
        // ImageView for displaying the user's profile image
        private var userImage: ImageView

        // Indicates whether the post is liked
        private var liked: Boolean = false
        // Indicates whether the post is saved
        private var saved: Boolean = false

        /**
         * Initializes the ImageViewHolder with the given itemView and listener.
         *
         * @param itemView The view corresponding to the item layout.
         * @param imageInItemClickListener The listener for image item click events.
         */
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
        }
    }
}