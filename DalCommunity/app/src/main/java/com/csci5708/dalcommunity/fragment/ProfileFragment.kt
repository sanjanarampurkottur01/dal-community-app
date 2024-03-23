package com.csci5708.dalcommunity.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.csci5708.dalcommunity.activity.AccountSettingsActivity
import com.csci5708.dalcommunity.activity.HomeActivity
import com.csci5708.dalcommunity.activity.ProfileDetailActivity
import com.csci5708.dalcommunity.activity.TempActivity
import com.csci5708.dalcommunity.activity.UserPostsActivity
import com.csci5708.dalcommunity.constants.AppConstants
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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

        val sharedPreferences =
            activity?.getSharedPreferences(AppConstants.APP_SHARED_PREFERENCES, MODE_PRIVATE)

        val profileEditButton: ImageView = view.findViewById(R.id.profile_page_edit_button)
        profileEditButton.setOnClickListener {
            val profileDetailIntent = Intent(activity, ProfileDetailActivity::class.java)
            activity?.startActivity(profileDetailIntent)
        }

        val accountSettingsImg: ImageView =
            view.findViewById(R.id.profile_page_account_settings_option)
        accountSettingsImg.setOnClickListener {
            val accountSettingsIntent = Intent(activity, AccountSettingsActivity::class.java)
            activity?.startActivity(accountSettingsIntent)
        }
        val accountSettingsTxt: TextView =
            view.findViewById(R.id.profile_page_account_settings_text)
        accountSettingsTxt.setOnClickListener {
            val accountSettingsIntent = Intent(activity, AccountSettingsActivity::class.java)
            activity?.startActivity(accountSettingsIntent)
        }
        val myPostsImg: ImageView = view.findViewById(R.id.profile_page_posts_option)
        myPostsImg.setOnClickListener {
            val userPostsActivityIntent = Intent(activity, UserPostsActivity::class.java)
            activity?.startActivity(userPostsActivityIntent)
        }
        val myPostsTxt: TextView = view.findViewById(R.id.profile_page_posts_text)
        myPostsTxt.setOnClickListener {
            val userPostsActivityIntent = Intent(activity, UserPostsActivity::class.java)
            activity?.startActivity(userPostsActivityIntent)
        }
        val profileLogoutButton: Button = view.findViewById(R.id.profile_page_log_out_button)
        profileLogoutButton.setOnClickListener {
            // TODO: REMOVE TEMP ACTIVITY BEFORE SUBMISSION
//            val intent = Intent(activity, TempActivity::class.java)
//            activity?.startActivity(intent)
            if (sharedPreferences != null) {
                if (sharedPreferences.getBoolean(AppConstants.SP_IS_SIGNED_IN_KEY, false)) {
                    val currentUser = Firebase.auth.currentUser
                    if (currentUser != null) {
                        Firebase.auth.signOut()
                        sharedPreferences.edit().putBoolean(AppConstants.SP_IS_SIGNED_IN_KEY, false)
                            .apply()
                    }
                    val intent = Intent(activity, HomeActivity::class.java)
                    activity?.startActivity(intent)
                }
            }
        }

        return view
    }
}