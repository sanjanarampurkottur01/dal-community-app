package com.csci5708.dalcommunity.util

import android.annotation.SuppressLint
import com.csci5708.dalcommunity.adapter.ClassesAdapter
import com.csci5708.dalcommunity.adapter.DaysRecyclerViewAdapter
import com.csci5708.dalcommunity.model.ClassDetails

object TimeTableSharedValues {
    var activeDayTab : Int = 0
    var classesToBeUsed : ArrayList<ClassDetails> = arrayListOf()
    var classesMap = mapOf<String,ArrayList<ClassDetails>>()
    @SuppressLint("StaticFieldLeak")
    lateinit var classesAdapter : ClassesAdapter
    @SuppressLint("StaticFieldLeak")
    lateinit var daysAdapter: DaysRecyclerViewAdapter
    var positionArrayPositionMap = mutableMapOf<Int, Int>()
    var classesStringArray = mutableMapOf<String, ArrayList<String>>()
    var pointer = 0
}