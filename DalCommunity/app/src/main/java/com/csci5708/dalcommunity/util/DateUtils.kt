package com.csci5708.dalcommunity.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtils {

    companion object {
        fun getDateTimeStamp() : String {
            val sdf = SimpleDateFormat("dd-MM-yyyyHH:mm:ss", Locale.getDefault())
            val currentDate = Date()
            return sdf.format(currentDate)
        }
    }
}