package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.PollPost
import com.example.dalcommunity.R

class PollPostsAdapter(val context: Context, var pollPosts: ArrayList<PollPost>) : RecyclerView.Adapter<PollPostsAdapter.ViewHolder>() {
    lateinit var view: View

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pollRecyclerView: RecyclerView
        private lateinit var pollAdapter: PollAdapter
        private val pollQuestion: TextView

        init {
            pollRecyclerView = itemView.findViewById(R.id.poll_recycler_view)
            pollQuestion = itemView.findViewById(R.id.poll_question_text)
        }

        fun setData(position: Int) {
            pollPosts[position].calculatePercentages()
            pollAdapter = PollAdapter(context, pollPosts[position])
            pollQuestion.text = pollPosts[position].pollQuestion
            pollRecyclerView.layoutManager = LinearLayoutManager(context)
            pollRecyclerView.adapter = pollAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.item_poll_posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pollPosts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(position)
    }
}