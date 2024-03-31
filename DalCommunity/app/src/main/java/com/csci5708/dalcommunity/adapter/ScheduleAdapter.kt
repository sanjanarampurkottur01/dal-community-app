package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

class ScheduleAdapter(private val context: Context) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {
    private lateinit var arrayOfWeeks: MutableList<Array<Int>>

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.day_card, parent, false)
        val currentDate = LocalDate.now().minusWeeks(1)
        val previousSunday = currentDate.with(DayOfWeek.SUNDAY)
        val nextSaturday = previousSunday.plusDays(6)

        arrayOfWeeks = mutableListOf()

        var currentDatePointer = previousSunday
        while (currentDatePointer <= nextSaturday) {
            val dateArray = arrayOf(currentDatePointer.dayOfMonth, currentDatePointer.monthValue)
            arrayOfWeeks.add(dateArray)
            currentDatePointer = currentDatePointer.plusDays(1)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 7
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTV = itemView.findViewById<TextView>(R.id.dayTV)
        val dayNumberTV = itemView.findViewById<TextView>(R.id.dateNumber)
        val monthTV = itemView.findViewById<TextView>(R.id.month)
        val yearTV = itemView.findViewById<TextView>(R.id.year)
    }
}