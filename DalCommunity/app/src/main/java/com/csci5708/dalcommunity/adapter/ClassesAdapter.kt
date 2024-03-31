package com.csci5708.dalcommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.ClassDetails
import com.csci5708.dalcommunity.util.TimeTableSharedValues
import com.example.dalcommunity.R
class ClassesAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var classDetailObjects = arrayListOf<ClassDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(context).inflate(R.layout.no_class_layout, parent, false)
            return NoClassViewHolder(view)
        } else  if (viewType == 1) {
            val view = LayoutInflater.from(context).inflate(R.layout.class_layout, parent, false)
            return ClassViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.no_classes_animation, parent, false)
            return NoClassAnimViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        classDetailObjects = TimeTableSharedValues.classesToBeUsed
        if (classDetailObjects.isEmpty()) {
            return 2
        }
        val anyClassesHere = anyClassesRightNow(position)
        if (anyClassesHere) {
            return 1
        }
        return 0
    }

    fun anyClassesRightNow(position: Int) : Boolean {
        for (classDetail in classDetailObjects) {
            var hours = Integer.parseInt(classDetail.startTime.substring(0,2))
            val minutes = Integer.parseInt(classDetail.startTime.substring(3,5))
            hours -= 6
            hours *= 2
            if (minutes == 30) {
                ++hours
            }
            if (hours == position) {
                return true
            }
        }
        return false
    }

    override fun getItemCount(): Int {
        classDetailObjects = TimeTableSharedValues.classesToBeUsed
        return if (classDetailObjects.isEmpty()) {
            1
        } else {
            32
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val classes = TimeTableSharedValues.classesToBeUsed
        if (holder is ClassViewHolder) {
            val ma = TimeTableSharedValues.positionArrayPositionMap
            if (TimeTableSharedValues.positionArrayPositionMap.keys.contains(position)) {
                holder.title.text = classes[TimeTableSharedValues.positionArrayPositionMap[position]!!].title
                holder.timeStart.text = classes[TimeTableSharedValues.positionArrayPositionMap[position]!!].startTime
                holder.timeEnd.text = classes[TimeTableSharedValues.positionArrayPositionMap[position]!!].endTime
            } else {
                holder.title.text = classes[TimeTableSharedValues.pointer].title
                holder.timeStart.text = classes[TimeTableSharedValues.pointer].startTime
                holder.timeEnd.text = classes[TimeTableSharedValues.pointer].endTime
                TimeTableSharedValues.positionArrayPositionMap[position] = TimeTableSharedValues.pointer
                TimeTableSharedValues.pointer += 1
            }
        }

        if (holder is NoClassViewHolder) {
            val constantMinutes = 360
            val timeHere = constantMinutes + (position * 30)
            var hour = (timeHere / 60).toString()
            var minute = (timeHere % 60).toString()
            if (hour.length == 1) {
                hour = "0$hour"
            }
            if (minute.length == 1) {
                minute = "0$minute"
            }
            val time = "$hour:$minute"
            holder.timeStart.text = time
        }
    }
    class NoClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timeStart = itemView.findViewById<TextView>(R.id.timeStart)
    }

    class NoClassAnimViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Animation is shown without the code having to do anything
    }

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.title)
        var timeStart = itemView.findViewById<TextView>(R.id.timeStart)
        var timeEnd = itemView.findViewById<TextView>(R.id.timeEnd)
    }
}