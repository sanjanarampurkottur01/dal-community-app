package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.csci5708.dalcommunity.model.ChatMessage
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatMessageAdapter(private var chats:List<ChatMessage>, private var context: Context):
    BaseAdapter(){
    
    /**
     * Returns the number of items in the list.
     *
     * @return the number of items in the list
     */
    override fun getCount(): Int {
        return chats.size
    }

    /**
     * Returns the item at the specified position in the list.
     *
     * @param position the position of the item in the list
     * @return the item at the specified position
     */
    override fun getItem(position: Int): ChatMessage {
        return chats[position]
    }

    /**
     * Returns the ID of the item at the specified position in the list.
     *
     * @param position the position of the item in the list
     * @return the ID of the item at the specified position
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

     /**
     * Returns a View that displays a chat message.
     *
     * @param position The position of the item in the list.
     * @param convertView The recycled View to populate.
     * @param parent The parent ViewGroup that the View will be attached to.
     * @return The View that displays the chat message.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val messageObj = getItem(position)
        val inflater = LayoutInflater.from(context)
        val currentUser = Firebase.auth.currentUser!!

        if (convertView == null) {
            convertView = inflater.inflate(
                if (messageObj?.sentById == currentUser.email!!) R.layout.message_right_item else R.layout.message_left_item,
                parent,
                false
            )
        }

        val senderNameTextView = convertView!!.findViewById<TextView>(R.id.senderNameTv)
        val messageTextView = convertView.findViewById<TextView>(R.id.senderMessageTv)
        val timestampTextView = convertView.findViewById<TextView>(R.id.sendTimeTv)

        // Populate data into TextViews
        senderNameTextView.text = messageObj?.sentByName
        messageTextView.text = messageObj?.message
        timestampTextView.text = timestampToDateTime(messageObj?.messageTime.toString())

        return convertView
    }

    /**
     * Converts a timestamp string to a formatted date and time string.
     *
     * @param timestampString The timestamp string to be converted.
     * @return The formatted date and time string.
     */
    fun timestampToDateTime(timestampString: String): String {
        try {
            val timestamp = timestampString.toLong()
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Updates the messages in the chat.
     *
     * @param newMessage the new list of chat messages to be updated
     */
    fun updateMessages(newMessage: List<ChatMessage>){
        this.chats=newMessage
        notifyDataSetChanged()
    }

}