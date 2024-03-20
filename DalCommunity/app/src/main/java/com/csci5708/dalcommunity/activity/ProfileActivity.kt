package com.csci5708.dalcommunity.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.csci5708.dalcommunity.fragment.ProfileFragment
import com.example.dalcommunity.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileFragment = ProfileFragment()
        val frTrans = supportFragmentManager.beginTransaction()
        frTrans.replace(
            R.id.profile_activity_frame,
            profileFragment
        ).commit()
    }
}