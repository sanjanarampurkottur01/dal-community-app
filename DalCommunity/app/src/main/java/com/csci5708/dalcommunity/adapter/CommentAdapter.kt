package com.csci5708.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.Comment
import com.example.dalcommunity.R

/**
 * Adapter for displaying a list of comments in a RecyclerView.
 *
 * @param comments The list of comments to be displayed.
 */
class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    /**
     * Called when RecyclerView needs a new [CommentViewHolder] of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [CommentViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_add_comment, parent, false)
        return CommentViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The [CommentViewHolder] which should be updated to represent the contents of the item at the given position
     * in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the adapter.
     */
    override fun getItemCount(): Int {
        return comments.size
    }
    /**
     * ViewHolder for individual comments in the RecyclerView.
     *
     * @param itemView The view corresponding to the item layout.
     */

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * Binds the comment data to the ViewHolder.
         *
         * @param comment The comment object to bind.
         */
        fun bind(comment: Comment) {
            TODO("Not yet implemented")
        }

    }
}
