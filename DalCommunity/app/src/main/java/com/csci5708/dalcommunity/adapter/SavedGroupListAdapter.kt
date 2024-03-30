package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.csci5708.dalcommunity.model.SavedPostGroup
import com.example.dalcommunity.R
import com.google.firebase.storage.FirebaseStorage

class SavedGroupListAdapter(private val context: Context, private val dataset: List<SavedPostGroup>) :
    RecyclerView.Adapter<SavedGroupListAdapter.ViewHolder>(){

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // A viewHolder to create the view for each list item
    class ViewHolder(view: View, private val itemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(view) {

        var cardImage: ImageView
        val titleView: TextView

        // Constructor
        init {
            cardImage = view.findViewById(R.id.save_group_item_image)
            titleView = view.findViewById(R.id.save_group_item_title)
        }
    }

    // The method that runs when the ViewHolder is created. Returns the inflated view
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.saved_group_item, viewGroup, false)

        return ViewHolder(view, itemClickListener)
    }

    // Method when a view holder is bound. Here we specify the data to be displayed inside each viewholder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val post = dataset[position]

        viewHolder.titleView.text = post.name
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val image = storageRef.child("/post-images/${post.posts[0]}.jpg")

        image.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            Glide.with(context)
                .load(imageUrl) // Use the imageUrl directly
                .error(R.drawable.error_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(viewHolder.cardImage)
        }.addOnFailureListener { exception ->
            // Handle any errors
        }

        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount() = dataset.size
}