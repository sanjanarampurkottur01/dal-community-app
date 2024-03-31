package com.csci5708.dalcommunity.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.csci5708.dalcommunity.firestore.FCMTokenManager
import com.csci5708.dalcommunity.fragment.CommunityFragments.CommunityFragment
import com.csci5708.dalcommunity.fragment.LostFoundFragment
import com.csci5708.dalcommunity.fragment.ScannerFragment
import com.csci5708.dalcommunity.fragment.SearchFragment
import com.csci5708.dalcommunity.fragment.TimelineFragment
import com.example.dalcommunity.R
import com.google.android.material.bottomsheet.BottomSheetBehavior


class HomeActivity : AppCompatActivity() {
    val SHARED_PREFERENCES = "sharedPref"
    val IS_SIGNED_IN = "isSignedIn"
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askToEnableNotificationsIfNeeded(this)

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, TimelineFragment())
            .commit()

        FCMTokenManager.updateOrStoreFCMToken(this)
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)


        val homeIcon = findViewById<ImageView>(R.id.home_icon)
        val timeTableIcon = findViewById<ImageView>(R.id.time_table_icon)
//        val //settingsIcon = findViewById<ImageView>(R.id.settings_icon)
        val userIcon = findViewById<ImageView>(R.id.user_icon)
        val petitionIcon = findViewById<ImageView>(R.id.petition_icon)
        val pokeIcon = findViewById<ImageView>(R.id.poke_icon)
        val interestsIcon = findViewById<ImageView>(R.id.common_interest_icon)
        val communityIcon = findViewById<ImageView>(R.id.community_icon)
        val lostFoundIcon = findViewById<ImageView>(R.id.lostFoundIcon)
        val userSearchIcon = findViewById<ImageView>(R.id.userSearchIcon)
        val scannerIcon = findViewById<ImageView>(R.id.scannerIcon)
        val announcementIcon = findViewById<ImageView>(R.id.announcementIcon)

        val bottomSheet = findViewById<FrameLayout>(R.id.bottom_sheet)
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 300
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        homeIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)
        }


        timeTableIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)
        }

        //settingsIcon.setOnClickListener{
//            homeIcon.setImageResource(R.drawable.home_outline)
//            timeTableIcon.setImageResource(R.drawable.time_table_outline)
//            //settingsIcon.setImageResource(R.drawable.settings)
//            userIcon.setImageResource(R.drawable.user_outline)
//            petitionIcon.setImageResource(R.drawable.petition_outline)
//            pokeIcon.setImageResource(R.drawable.poke_outline)
//            interestsIcon.setImageResource(R.drawable.like_outline)
//            communityIcon.setImageResource(R.drawable.groups_outline)
//            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
//            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
//            scannerIcon.setImageResource(R.drawable.scanner_outline)
//
//        }

        userIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)

            val profileActivityIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileActivityIntent)
        }

        petitionIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)

            val profileActivityIntent = Intent(this, PetitionActivity::class.java)
            startActivity(profileActivityIntent)
        }
        pokeIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_filled)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)

            val profileActivityIntent = Intent(this, PokeActivity::class.java)
            startActivity(profileActivityIntent)
        }

        interestsIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)

            val profileActivityIntent = Intent(this, CommonInterestsActivity::class.java)
            startActivity(profileActivityIntent)
        }

        communityIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            communityIcon.setImageResource(R.drawable.groups_baseline)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)

            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, CommunityFragment())
                .addToBackStack(null)
                .commit()
        }
        lostFoundIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            lostFoundIcon.setImageResource(R.drawable.twotone_content_paste_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)

            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, LostFoundFragment())
                .addToBackStack(null)
                .commit()
        }
        userSearchIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.baseline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)

            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, SearchFragment())
                .addToBackStack(null)
                .commit()
        }

        scannerIcon.setOnClickListener {
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.baseline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner)
            announcementIcon.setImageResource(R.drawable.icon_announcement_outline)


            val scannerFragment = ScannerFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, scannerFragment)
                .addToBackStack(null)
                .commit()
        }

        announcementIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.baseline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            announcementIcon.setImageResource(R.drawable.icon_announcement)

            val intent = Intent(this@HomeActivity, AnnouncementActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkNotificationEnabled(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle("Enable Notifications")
            setMessage("To receive notifications, please enable them in your device settings.")
            setPositiveButton("Settings") { _, _ ->
                // User clicked Settings button, open app notification settings
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                // User clicked Cancel button, dismiss dialog
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun askToEnableNotificationsIfNeeded(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        if (!areNotificationsEnabled(notificationManager)) {
            checkNotificationEnabled(context)
        }
    }
    private fun areNotificationsEnabled(notificationManager: NotificationManagerCompat) = when {
        notificationManager.areNotificationsEnabled().not() -> false
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            notificationManager.notificationChannels.firstOrNull { channel ->
                channel.importance == NotificationManager.IMPORTANCE_NONE
            } == null
        }
        else -> true
    }
}
