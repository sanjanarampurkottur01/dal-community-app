package com.csci5708.dalcommunity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.DaysRecyclerViewAdapter
import com.example.dalcommunity.R


class TimeTableFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_table, container, false)
        val daysRecyclerView = view.findViewById<RecyclerView>(R.id.daysRecyclerView)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        daysRecyclerView.layoutManager = layoutManager
        val adapter = activity?.let { DaysRecyclerViewAdapter(it.baseContext) }
        daysRecyclerView.adapter = adapter
        return view
    }
}