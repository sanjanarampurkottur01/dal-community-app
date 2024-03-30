package com.csci5708.dalcommunity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ReportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.ReportOptionsAdapter
import com.example.dalcommunity.R

class ReportFragment : Fragment(), ReportOptionsAdapter.OnReportOptionClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reportOptionsList = view.findViewById<RecyclerView>(R.id.report_list)
        val reportOptionValues = resources.getStringArray(R.array.report_options)
        val reportAdapter = ReportOptionsAdapter(requireActivity(), reportOptionValues, this)
        reportOptionsList.layoutManager = LinearLayoutManager(requireActivity())
        reportOptionsList.adapter = reportAdapter
    }

    override fun onReportOptionClicked(position: Int) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, ReportSuccessFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentManager.popBackStack()
        fragmentTransaction.commit()
    }
}