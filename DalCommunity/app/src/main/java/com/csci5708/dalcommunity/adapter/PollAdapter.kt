package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.PollPost
import com.example.dalcommunity.R

class PollAdapter(val context: Context, private var pollPost: PollPost) :
    RecyclerView.Adapter<PollAdapter.ViewHolder>() {
    lateinit var view: View
    private var previousPosition = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pollRelativeLayout: RelativeLayout
        var pollSeekBar: SeekBar
        var pollTitle: TextView
        var pollPercentage: TextView
        private var pollHeader: TextView

        init {
            pollRelativeLayout = itemView.findViewById(R.id.poll_relative_layout)
            pollSeekBar = itemView.findViewById(R.id.poll_seekbar)
            pollTitle = itemView.findViewById(R.id.poll_title_text)
            pollPercentage = itemView.findViewById(R.id.poll_percentage_text)
            pollHeader = itemView.findViewById(R.id.poll_header)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollAdapter.ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.item_single_poll_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PollAdapter.ViewHolder, position: Int) {
        val pollValue = pollPost.pollValuesList[position]
        if (pollPost.isUserVoteComplete)
            previousPosition = pollPost.getVotedOptionPosition()
        // NOTE: This onTouchListener is required to prevent the user from moving the seek bar
        holder.pollSeekBar.setOnTouchListener { v, event ->
            when (event.action) {
                else -> {
                    true
                }
            }
        }
        holder.pollTitle.tag = position
        holder.pollTitle.text = pollValue.title
        holder.pollSeekBar.progress = pollValue.progress
        holder.pollPercentage.text = pollValue.percentage
        if (!pollValue.isSelected) {
            holder.pollTitle.setOnClickListener {
                val currentPosition: Int = holder.pollTitle.tag as Int
                if (previousPosition == -1) {
                    pollPost.updateVote(currentPosition, context)
                    previousPosition = currentPosition
                    holder.pollSeekBar.progress = pollValue.progress
                    holder.pollSeekBar.progressDrawable =
                        ContextCompat.getDrawable(context, R.drawable.poll_highlighted_drawable)
                    holder.pollPercentage.text = pollValue.percentage
                    pollValue.isSelected = true
                    // Need to redraw all the poll options because percentages would have changed
                    notifyDataSetChanged()
                }
            }
        } else {
            holder.pollSeekBar.progress = pollValue.progress
            holder.pollSeekBar.progressDrawable =
                ContextCompat.getDrawable(context, R.drawable.poll_highlighted_drawable)
        }
    }

    override fun getItemCount(): Int {
        return pollPost.pollValuesSize
    }
}