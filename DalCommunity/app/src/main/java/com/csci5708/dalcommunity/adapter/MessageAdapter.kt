package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.Message
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVED = 1
    val ITEM_SENT = 2

    /**
     * Create view holder based on view type.
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return RecyclerView.ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            // Inflate received layout
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_received_layout, parent, false)
            return ReceivedMessageViewHolder(view)
        } else{
            // Inflate sent layout
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_sent_layout, parent, false)
            return SentMessageViewHolder(view)
        }
    }

    /**
     * Get total item count.
     * @return Int
     */
    override fun getItemCount(): Int {
        return messageList.size
    }

    /**
     * Get item view type.
     * @param position The position of the item.
     * @return Int
     */
    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (Firebase.auth.currentUser?.email.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVED
        }
    }

    /**
     * Bind data to the view holder.
     * @param holder The view holder.
     * @param position The position in the list.
     * @return Unit
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass == SentMessageViewHolder::class.java){
            // For sent message view holder
            val viewHolder = holder as SentMessageViewHolder
            holder.sentMessage.text = currentMessage.message
        }else{
            // For received message view holder
            val viewHolder = holder as ReceivedMessageViewHolder
            holder.receivedMessage.text = currentMessage.message
        }
    }

    /**
     * View holder for sent messages.
     */
    class SentMessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        val sentMessage = view.findViewById<TextView>(R.id.tvSentMessage)
    }

    /**
     * View holder for received messages.
     */
    class ReceivedMessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        val receivedMessage = view.findViewById<TextView>(R.id.tvReceivedMessage)
    }
}