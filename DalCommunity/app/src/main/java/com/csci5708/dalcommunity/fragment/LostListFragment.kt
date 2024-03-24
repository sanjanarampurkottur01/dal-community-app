package com.csci5708.dalcommunity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dalcommunity.Place
import com.example.dalcommunity.R
import com.example.dalcommunity.UserMap

class LostListFragment: Fragment() {
    private lateinit var locButton: Button;
    private lateinit var userMap: UserMap
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lost_listing, container, false)
        locButton = view.findViewById(R.id.locbutton)
        locButton.setOnClickListener {
             userMap = UserMap(
                "Memories from University",
                mutableListOf(
                    Place("Branner Hall","Describe me" ,37.426, -122.163),
                    Place("Gates CS building", "Describe me",37.430, -122.173)
                )
            )
//            var intent = Intent(activity,MapsActivity::class.java)
//            intent.putExtra("EXTRA_USER_MAP",userMap)
//            activity?.startActivity(intent)
//            val mapFragment = MapsFragment()
            val mapFragment = AddMarkerFragment()
            val bundle = Bundle()
            bundle.putSerializable("EXTRA_USER_MAP", userMap)
            mapFragment.arguments = bundle

            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragment_container, mapFragment)
                addToBackStack(null) // Add this line if you want to add the transaction to the back stack
                commit()

            }

        }
        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            val result = bundle.getInt("resultData")
            if(bundle.containsKey("EXTRA_USER_MAP")) {
                userMap = bundle.getSerializable("EXTRA_USER_MAP") as UserMap
                var textView:TextView =view.findViewById(R.id.textView2);
                textView.setText(userMap.toString())
            }

            // Handle the result. For example, you could update UI or initiate some action based on 'result'.
        }
        return view;
    }
}