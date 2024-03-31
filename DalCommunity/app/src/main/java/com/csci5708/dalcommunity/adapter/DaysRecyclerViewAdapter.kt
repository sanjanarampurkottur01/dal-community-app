package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.util.TimeTableSharedValues
import com.example.dalcommunity.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale


class DaysRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<DaysRecyclerViewAdapter.ViewHolder>() {
    private lateinit var arrayOfWeeks: MutableList<Array<Int>>
    private var arrayOfMonths = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    private var arrayOfDays = arrayOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(context).inflate(R.layout.day_card, parent, false)
            view.setBackgroundResource(R.drawable.day_card_background)
            view.findViewById<TextView>(R.id.year).setTextColor(context.getColor(R.color.text_color))
            view.findViewById<TextView>(R.id.month).setTextColor(context.getColor(R.color.text_color))
            view.findViewById<TextView>(R.id.dateNumber).setTextColor(context.getColor(R.color.text_color))
            view.findViewById<TextView>(R.id.dayTV).setTextColor(context.getColor(R.color.text_color))
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.day_card, parent, false)
            view.setBackgroundResource(R.drawable.day_card_selected)
            view.findViewById<TextView>(R.id.year).setTextColor(context.getColor(R.color.white))
            view.findViewById<TextView>(R.id.month).setTextColor(context.getColor(R.color.white))
            view.findViewById<TextView>(R.id.dateNumber).setTextColor(context.getColor(R.color.white))
            view.findViewById<TextView>(R.id.dayTV).setTextColor(context.getColor(R.color.white))
            return ViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val previousSunday = LocalDate
            .now()
            .with(
                TemporalAdjusters.previousOrSame( DayOfWeek.SUNDAY )
            ) ;
        val nextSaturday = previousSunday.plusDays(6)


        arrayOfWeeks = mutableListOf()

        var currentDatePointer = previousSunday
        while (currentDatePointer <= nextSaturday) {
            val dateArray = arrayOf(currentDatePointer.dayOfMonth, currentDatePointer.monthValue)
            arrayOfWeeks.add(dateArray)
            currentDatePointer = currentDatePointer.plusDays(1)
        }
        if (position == TimeTableSharedValues.activeDayTab) {
            return 1
        } else {
            return 0
        }
    }

    override fun getItemCount(): Int {
        return 7
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayTV.text = arrayOfDays[position]
        holder.dayNumberTV.text = arrayOfWeeks[position][0].toString()
        holder.monthTV.text = arrayOfMonths[arrayOfWeeks[position][1] - 1]
        holder.yearTV.text = Calendar.getInstance().get(Calendar.YEAR).toString()
        holder.itemView.setOnClickListener{
            if (TimeTableSharedValues.classesMap[getCurrentDay(position)] !=  null) {
                TimeTableSharedValues.classesToBeUsed = TimeTableSharedValues.classesMap[getCurrentDay(position)]!!
            } else {
                TimeTableSharedValues.classesToBeUsed = arrayListOf()
            }
            TimeTableSharedValues.activeDayTab = position
            TimeTableSharedValues.classesAdapter.notifyDataSetChanged()
            TimeTableSharedValues.daysAdapter.notifyDataSetChanged()
            TimeTableSharedValues.pointer = 0
            TimeTableSharedValues.positionArrayPositionMap = mutableMapOf()
        }
    }

    fun getCurrentDay(dayNumber: Int) : String {
        val arrayOfDays = arrayOf("sunday","monday","tuesday","wednesday","thursday","friday","saturday")
        return arrayOfDays[dayNumber]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTV = itemView.findViewById<TextView>(R.id.dayTV)
        val dayNumberTV = itemView.findViewById<TextView>(R.id.dateNumber)
        val monthTV = itemView.findViewById<TextView>(R.id.month)
        val yearTV = itemView.findViewById<TextView>(R.id.year)
    }
}