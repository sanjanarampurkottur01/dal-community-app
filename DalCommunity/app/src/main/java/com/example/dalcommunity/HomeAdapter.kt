package com.example.dalcommunity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HomeAdapter(private val context: Context, private val posts: List<String>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
//        return posts.size
        return 5 // For now, use the line above for the long run
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Logic to set data from the actual post
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //
    }
}