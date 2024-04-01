package com.csci5708.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.Announcement
import com.example.dalcommunity.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnnouncementAdapter(private var announcements: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    /**
     * Create view holder for the announcement item.
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    /**
     * Bind data to the view holder.
     * @param holder The view holder.
     * @param position The position in the list.
     * @return Unit
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.title.text = announcement.title
        holder.content.text = announcement.content
        holder.nameAndTime.text = "${announcement.senderName}, ${announcement.timestamp?.let { formatTimestamp(it) }}"
    }

    /**
     * Get total item count.
     * @return Int
     */
    override fun getItemCount(): Int {
        return announcements.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tvTitle)
        val content = itemView.findViewById<TextView>(R.id.tvAnnouncementMessage)
        val nameAndTime = itemView.findViewById<TextView>(R.id.tvNameAndTime)
    }

    /**
     * Format timestamp to human-readable format.
     * @param timestamp The timestamp.
     * @return String
     */
    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    /**
     * Update data in the adapter.
     * @param newAnnouncements The new list of announcements.
     * @return Unit
     */
    fun updateData(newAnnouncements: List<Announcement>) {
        announcements = newAnnouncements
        notifyDataSetChanged()
    }
}