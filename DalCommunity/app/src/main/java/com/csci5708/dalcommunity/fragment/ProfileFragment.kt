package com.csci5708.dalcommunity.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.csci5708.dalcommunity.activity.ProfileDetailActivity
import com.example.dalcommunity.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val profileEditButton: ImageView = view.findViewById(R.id.profile_page_edit_button)
        profileEditButton.setOnClickListener {
            val profileDetailIntent = Intent(activity, ProfileDetailActivity::class.java)
            activity?.startActivity(profileDetailIntent)
        }

        return view
    }
}