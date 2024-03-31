package com.csci5708.dalcommunity.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.squareup.picasso.Picasso

class UserDetailsFragment: Fragment() {
    private lateinit var profilImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var emailLine:View
    private lateinit var emailGroup:LinearLayout
    private lateinit var emailTextView: TextView
    private lateinit var descriptionLine:View
    private lateinit var descriptionGroup:LinearLayout
    private lateinit var descriptionTextView: TextView
    private lateinit var interestLine:View
    private lateinit var interestGroup:LinearLayout
    private lateinit var interestsTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_details, container, false)
        userNameTextView = view.findViewById(R.id.userNameTextView)
        profilImageView = view.findViewById(R.id.profileImage)
        emailLine = view.findViewById(R.id.emailLine)
        emailGroup = view.findViewById(R.id.emailGroup)
        emailTextView = view.findViewById(R.id.emailTextView)
        descriptionLine = view.findViewById(R.id.descriptionLine)
        descriptionGroup = view.findViewById(R.id.descriptionGroup)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        interestLine = view.findViewById(R.id.interestLine)
        interestGroup = view.findViewById(R.id.interestGroup)
        interestsTextView = view.findViewById(R.id.interestsTextView)
        arguments?.let {
            if(it.containsKey("EXTRA_USER")) {
                populateViews(it.getSerializable("EXTRA_USER") as User)
            }
        }
        return view
    }

    private fun populateViews(user: User) {
        userNameTextView.setText(user.name)
        if(!user.photoUri.isNullOrEmpty()) {
            Picasso.get().load(user.photoUri).into(profilImageView)
        }
        if(user.email.isNullOrEmpty()) {
            emailLine.visibility = View.GONE
            emailGroup.visibility = View.GONE
        }
        else {
            emailTextView.setText(user.email)
        }
        if(user.description.isNullOrEmpty()) {
            descriptionLine.visibility = View.GONE
            descriptionGroup.visibility = View.GONE
        }
        else {
            descriptionTextView.setText(user.description)
        }
        var interests = mutableListOf<String>()
        if(!user.firstInterest.isNullOrEmpty()) {
            interests.add(user.firstInterest)
        }
        if(!user.secondInterest.isNullOrEmpty()) {
            interests.add(user.secondInterest)
        }
        if(!user.thirdInterest.isNullOrEmpty()){
            interests.add(user.thirdInterest)
        }
        if(interests.size==0) {
            interestLine.visibility = View.GONE
            interestGroup.visibility = View.GONE
        }
        else {
            Toast.makeText(requireContext(),interests.toString(),Toast.LENGTH_SHORT).show()
            interestsTextView.setText(interests.joinToString { it })
        }
    }
}