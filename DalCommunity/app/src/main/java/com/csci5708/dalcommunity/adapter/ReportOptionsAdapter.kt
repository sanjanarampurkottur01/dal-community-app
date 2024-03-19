package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R

class ReportOptionsAdapter(private val context: Context, private val reportReasons: Array<String>) :
    RecyclerView.Adapter<ReportOptionsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.report_reason_item, parent, false)

        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return reportReasons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reason = reportReasons[position]

        holder.reportTitle.text = reason
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var reportTitle: TextView

        init {
            reportTitle = itemView.findViewById(R.id.reason_title)
        }
    }
}