package com.csci5708.dalcommunity.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.ClassesAdapter
import com.csci5708.dalcommunity.adapter.DaysRecyclerViewAdapter
import com.csci5708.dalcommunity.model.ClassDetails
import com.csci5708.dalcommunity.util.TimeTableSharedValues
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import nl.joery.timerangepicker.TimeRangePicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

/**
 * Fragment for viewing, adding and following classes.
 */
class TimeTableFragment : Fragment() {
    var classes : ArrayList<String> = arrayListOf()
    var classDetailMap = mutableMapOf<String, ArrayList<ClassDetails>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val docRef = Firebase.firestore.collection("classes").document(FirebaseAuth.getInstance().currentUser?.email.toString())
        var currentDate = LocalDate.now()
        var todaysDay = currentDate.dayOfWeek.name.toLowerCase(Locale.ROOT)
        when (todaysDay) {
            "monday" -> {
                TimeTableSharedValues.activeDayTab = 1
            }
            "tuesday" -> {
                TimeTableSharedValues.activeDayTab = 2
            }
            "wednesday" -> {
                TimeTableSharedValues.activeDayTab = 3
            }
            "thursday" -> {
                TimeTableSharedValues.activeDayTab = 4
            }
            "friday" -> {
                TimeTableSharedValues.activeDayTab = 5
            }
            "saturday" -> {
                TimeTableSharedValues.activeDayTab = 6
            }
            "sunday" -> {
                TimeTableSharedValues.activeDayTab = 0
            }
        }
        getFirebaseValues(docRef, todaysDay)
    }

    /**
     * Get the class details from firebase
     */
    private fun getFirebaseValues(
        docRef: DocumentReference,
        todaysDay: String
    ) {
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.contains("monday")) {
                        classes = document.data?.get("monday") as ArrayList<String>
                        val classDetailList: ArrayList<ClassDetails> = arrayListOf()
                        val classDetailStringList: ArrayList<String> = arrayListOf()
                        for (string in classes) {
                            classDetailList.add(
                                ClassDetails(
                                    string.substring(0, 2),
                                    string.substring(2, 7),
                                    string.substring(7, 12),
                                    string.substring(12, string.length)
                                )
                            )
                            classDetailStringList.add(string)
                        }
                        TimeTableSharedValues.classesStringArray.put(
                            "monday",
                            classDetailStringList
                        )
                        classDetailMap["monday"] = sortTimeStrings(classDetailList)
                        if (todaysDay.contentEquals("monday")) {
                            TimeTableSharedValues.classesToBeUsed = classDetailMap["monday"]!!
                        }
                    }
                    if (document.contains("tuesday")) {
                        classes = document.data?.get("tuesday") as ArrayList<String>
                        val classDetailList: ArrayList<ClassDetails> = arrayListOf()
                        val classDetailStringList: ArrayList<String> = arrayListOf()
                        for (string in classes) {
                            classDetailList.add(
                                ClassDetails(
                                    string.substring(0, 2),
                                    string.substring(2, 7),
                                    string.substring(7, 12),
                                    string.substring(12, string.length)
                                )
                            )
                            classDetailStringList.add(string)
                        }
                        TimeTableSharedValues.classesStringArray.put(
                            "tuesday",
                            classDetailStringList
                        )
                        classDetailMap["tuesday"] = sortTimeStrings(classDetailList)
                        if (todaysDay.contentEquals("tuesday")) {
                            TimeTableSharedValues.classesToBeUsed = classDetailMap["tuesday"]!!
                        }
                    }
                    if (document.contains("wednesday")) {
                        classes = document.data?.get("wednesday") as ArrayList<String>
                        val classDetailList: ArrayList<ClassDetails> = arrayListOf()
                        val classDetailStringList: ArrayList<String> = arrayListOf()
                        for (string in classes) {
                            classDetailList.add(
                                ClassDetails(
                                    string.substring(0, 2),
                                    string.substring(2, 7),
                                    string.substring(7, 12),
                                    string.substring(12, string.length)
                                )
                            )
                            classDetailStringList.add(string)
                        }
                        TimeTableSharedValues.classesStringArray.put(
                            "wednesday",
                            classDetailStringList
                        )
                        classDetailMap["wednesday"] = sortTimeStrings(classDetailList)
                        if (todaysDay.contentEquals("wednesday")) {
                            TimeTableSharedValues.classesToBeUsed = classDetailMap["wednesday"]!!
                        }
                    }
                    if (document.contains("thursday")) {
                        classes = document.data?.get("thursday") as ArrayList<String>
                        val classDetailList: ArrayList<ClassDetails> = arrayListOf()
                        val classDetailStringList: ArrayList<String> = arrayListOf()
                        for (string in classes) {
                            classDetailList.add(
                                ClassDetails(
                                    string.substring(0, 2),
                                    string.substring(2, 7),
                                    string.substring(7, 12),
                                    string.substring(12, string.length)
                                )
                            )
                            classDetailStringList.add(string)
                        }
                        TimeTableSharedValues.classesStringArray.put(
                            "thursday",
                            classDetailStringList
                        )
                        classDetailMap["thursday"] = sortTimeStrings(classDetailList)
                        if (todaysDay.contentEquals("thursday")) {
                            TimeTableSharedValues.classesToBeUsed = classDetailMap["thursday"]!!
                        }
                    }
                    if (document.contains("friday")) {
                        classes = document.data?.get("friday") as ArrayList<String>
                        val classDetailList: ArrayList<ClassDetails> = arrayListOf()
                        val classDetailStringList: ArrayList<String> = arrayListOf()
                        for (string in classes) {
                            classDetailList.add(
                                ClassDetails(
                                    string.substring(0, 2),
                                    string.substring(2, 7),
                                    string.substring(7, 12),
                                    string.substring(12, string.length)
                                )
                            )
                            classDetailStringList.add(string)
                        }
                        TimeTableSharedValues.classesStringArray.put(
                            "friday",
                            classDetailStringList
                        )
                        classDetailMap["friday"] = sortTimeStrings(classDetailList)
                        if (todaysDay.contentEquals("friday")) {
                            TimeTableSharedValues.classesToBeUsed = classDetailMap["friday"]!!
                        }
                    }
                    if (document.contains("saturday")) {
                        classes = document.data?.get("saturday") as ArrayList<String>
                        val classDetailList: ArrayList<ClassDetails> = arrayListOf()
                        val classDetailStringList: ArrayList<String> = arrayListOf()
                        for (string in classes) {
                            classDetailList.add(
                                ClassDetails(
                                    string.substring(0, 2),
                                    string.substring(2, 7),
                                    string.substring(7, 12),
                                    string.substring(12, string.length)
                                )
                            )
                            classDetailStringList.add(string)
                        }
                        TimeTableSharedValues.classesStringArray.put(
                            "saturday",
                            classDetailStringList
                        )
                        classDetailMap["saturday"] = sortTimeStrings(classDetailList)
                        if (todaysDay.contentEquals("saturday")) {
                            TimeTableSharedValues.classesToBeUsed = classDetailMap["saturday"]!!
                        }
                    }
                    if (document.contains("sunday")) {
                        classes = document.data?.get("sunday") as ArrayList<String>
                        val classDetailList: ArrayList<ClassDetails> = arrayListOf()
                        val classDetailStringList: ArrayList<String> = arrayListOf()
                        for (string in classes) {
                            classDetailList.add(
                                ClassDetails(
                                    string.substring(0, 2),
                                    string.substring(2, 7),
                                    string.substring(7, 12),
                                    string.substring(12, string.length)
                                )
                            )
                            classDetailStringList.add(string)
                        }
                        TimeTableSharedValues.classesStringArray.put(
                            "sunday",
                            classDetailStringList
                        )
                        classDetailMap["sunday"] = sortTimeStrings(classDetailList)
                        if (todaysDay.contentEquals("sunday")) {
                            TimeTableSharedValues.classesToBeUsed = classDetailMap["sunday"]!!
                        }
                    }
                    TimeTableSharedValues.classesMap = classDetailMap
                } else {
                    Log.d("", "")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("", "get failed with ", exception)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_table, container, false)
        val daysRecyclerView = view.findViewById<RecyclerView>(R.id.daysRecyclerView)
        val classesRecyclerView = view.findViewById<RecyclerView>(R.id.daysSchedule)
        val addClasses = view.findViewById<ImageView>(R.id.add_classes)

        addClasses.setOnClickListener{
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(resources.getDrawable(R.drawable.dialog_background))
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.add_class_dialog)

            val mondayIcon = dialog.findViewById<ImageView>(R.id.monday)
            val tuesdayIcon = dialog.findViewById<ImageView>(R.id.tuesday)
            val wednesdayIcon = dialog.findViewById<ImageView>(R.id.wednesday)
            val thursdayIcon = dialog.findViewById<ImageView>(R.id.thursday)
            val fridayIcon = dialog.findViewById<ImageView>(R.id.friday)
            val saturdayIcon = dialog.findViewById<ImageView>(R.id.saturday)
            val sundayIcon = dialog.findViewById<ImageView>(R.id.sunday)

            val classNameET = dialog.findViewById<EditText>(R.id.className)
            val timeRangePicker = dialog.findViewById<TimeRangePicker>(R.id.timeRangePicker)

            var weekDay = "mo"
            var weekDayFull = "monday"

            mondayIcon.setOnClickListener{
                mondayIcon.setImageResource(R.drawable.monday_selected)
                tuesdayIcon.setImageResource(R.drawable.tuesday)
                wednesdayIcon.setImageResource(R.drawable.wednesday)
                thursdayIcon.setImageResource(R.drawable.tuesday)
                fridayIcon.setImageResource(R.drawable.friday)
                saturdayIcon.setImageResource(R.drawable.saturday)
                sundayIcon.setImageResource(R.drawable.saturday)
                weekDay = "mo"
                weekDayFull = "monday"
            }

            tuesdayIcon.setOnClickListener{
                mondayIcon.setImageResource(R.drawable.monday)
                tuesdayIcon.setImageResource(R.drawable.tuesday_selected)
                wednesdayIcon.setImageResource(R.drawable.wednesday)
                thursdayIcon.setImageResource(R.drawable.tuesday)
                fridayIcon.setImageResource(R.drawable.friday)
                saturdayIcon.setImageResource(R.drawable.saturday)
                sundayIcon.setImageResource(R.drawable.saturday)
                weekDay = "tu"
                weekDayFull = "tuesday"
            }

            wednesdayIcon.setOnClickListener{
                mondayIcon.setImageResource(R.drawable.monday)
                tuesdayIcon.setImageResource(R.drawable.tuesday)
                wednesdayIcon.setImageResource(R.drawable.wednesday_selected)
                thursdayIcon.setImageResource(R.drawable.tuesday)
                fridayIcon.setImageResource(R.drawable.friday)
                saturdayIcon.setImageResource(R.drawable.saturday)
                sundayIcon.setImageResource(R.drawable.saturday)
                weekDay = "we"
                weekDayFull = "wednesday"
            }

            thursdayIcon.setOnClickListener{
                mondayIcon.setImageResource(R.drawable.monday)
                tuesdayIcon.setImageResource(R.drawable.tuesday)
                wednesdayIcon.setImageResource(R.drawable.wednesday)
                thursdayIcon.setImageResource(R.drawable.tuesday_selected)
                fridayIcon.setImageResource(R.drawable.friday)
                saturdayIcon.setImageResource(R.drawable.saturday)
                sundayIcon.setImageResource(R.drawable.saturday)
                weekDay = "th"
                weekDayFull = "thursday"
            }

            fridayIcon.setOnClickListener{
                mondayIcon.setImageResource(R.drawable.monday)
                tuesdayIcon.setImageResource(R.drawable.tuesday)
                wednesdayIcon.setImageResource(R.drawable.wednesday)
                thursdayIcon.setImageResource(R.drawable.tuesday)
                fridayIcon.setImageResource(R.drawable.friday_selected)
                saturdayIcon.setImageResource(R.drawable.saturday)
                sundayIcon.setImageResource(R.drawable.saturday)
                weekDay = "fr"
                weekDayFull = "friday"
            }

            saturdayIcon.setOnClickListener{
                mondayIcon.setImageResource(R.drawable.monday)
                tuesdayIcon.setImageResource(R.drawable.tuesday)
                wednesdayIcon.setImageResource(R.drawable.wednesday)
                thursdayIcon.setImageResource(R.drawable.tuesday)
                fridayIcon.setImageResource(R.drawable.friday)
                saturdayIcon.setImageResource(R.drawable.saturday_selected)
                sundayIcon.setImageResource(R.drawable.saturday)
                weekDay = "sa"
                weekDayFull = "saturday"
            }

            sundayIcon.setOnClickListener{
                mondayIcon.setImageResource(R.drawable.monday)
                tuesdayIcon.setImageResource(R.drawable.tuesday)
                wednesdayIcon.setImageResource(R.drawable.wednesday)
                thursdayIcon.setImageResource(R.drawable.tuesday)
                fridayIcon.setImageResource(R.drawable.friday)
                saturdayIcon.setImageResource(R.drawable.saturday)
                sundayIcon.setImageResource(R.drawable.saturday_selected)
                weekDay = "su"
                weekDayFull = "sunday"
            }

            val dialogButton = dialog.findViewById<View>(R.id.addClassBtn) as Button
            dialogButton.setOnClickListener {
                if (classNameET.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please provide a class name", Toast.LENGTH_SHORT).show()
                } else {
                    var startHour = timeRangePicker.startTime.hour.toString()
                    var startMinute = timeRangePicker.startTime.minute.toString()
                    var endHour = timeRangePicker.endTime.hour.toString()
                    var endMinute = timeRangePicker.endTime.minute.toString()
                    if (startHour.length == 1) {
                        startHour = "0$startHour"
                    }
                    if (startMinute.length == 1) {
                        startMinute = "0$startMinute"
                    }
                    if (endHour.length == 1) {
                        endHour = "0$endHour"
                    }
                    if (endMinute.length == 1) {
                        endMinute = "0$endMinute"
                    }
                    val classDetails = weekDay + startHour + ":" + startMinute + endHour + ":" + endMinute + classNameET.text.toString()
                    var classesThisDay = TimeTableSharedValues.classesStringArray[weekDayFull]
                    if (classesThisDay.isNullOrEmpty()) {
                        classesThisDay = arrayListOf()
                    }
                    classesThisDay.add(classDetails)
                    val map : Map<String, ArrayList<String>?> = mapOf(weekDayFull to classesThisDay)
                    Firebase.firestore.collection("classes").document(FirebaseAuth.getInstance().currentUser?.email.toString())
                        .set(map, SetOptions.merge())
                    dialog.dismiss()
                    Toast.makeText(context, "Class Added!", Toast.LENGTH_SHORT).show()
                    val docRef = Firebase.firestore.collection("classes").document(FirebaseAuth.getInstance().currentUser?.email.toString())
                    val todaysDay = LocalDate.now().dayOfWeek.name.toLowerCase(Locale.ROOT)
                    getFirebaseValues(docRef, todaysDay)
                    TimeTableSharedValues.classesAdapter.notifyDataSetChanged()
                    TimeTableSharedValues.daysAdapter.notifyDataSetChanged()
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1
                        )
                    }
                }
            }

            dialog.show()
        }

        var layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        daysRecyclerView.layoutManager = layoutManager
        var adapter = activity?.let { DaysRecyclerViewAdapter(it.baseContext) }
        daysRecyclerView.adapter = adapter
        TimeTableSharedValues.daysAdapter = adapter!!

        val classeslayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        classesRecyclerView.layoutManager = classeslayoutManager
        val classesAdapter = context?.let { ClassesAdapter(it) }
        classesRecyclerView.adapter = classesAdapter
        TimeTableSharedValues.classesAdapter = classesAdapter!!
        return view
    }

    /**
     * Sort the classes based on the timings as the timings are stored as strings
     */
    fun sortTimeStrings(timeStrings: ArrayList<ClassDetails>): ArrayList<ClassDetails> {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sortedTimeStrings = timeStrings.sortedBy { timeString ->
            val time = timeString.startTime
            sdf.parse(time)
        }
        var al = arrayListOf<ClassDetails>()
        al.addAll(sortedTimeStrings)
        return al
    }
}