package com.example.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.model.Comment
import com.example.dalcommunity.R

class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_add_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comment: Comment) {
            TODO("Not yet implemented")
        }

    }
}
