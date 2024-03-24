package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.dalcommunity.R

class ImageSelectAdapter(private val context: Context, private val images: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.image_grid, parent, false)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.imageView)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        Glide.with(context)
            .load(images[position])
            .into(holder.imageView)

        return view
    }

    private class ViewHolder {
        lateinit var imageView: ImageView
    }
}
