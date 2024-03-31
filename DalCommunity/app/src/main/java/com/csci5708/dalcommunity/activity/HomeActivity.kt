package com.csci5708.dalcommunity.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.csci5708.dalcommunity.firestore.FCMTokenManager
import com.csci5708.dalcommunity.fragment.BroadcastQuestionFragment
import com.csci5708.dalcommunity.fragment.CommunityFragments.CommunityFragment
import com.csci5708.dalcommunity.fragment.LostFoundFragment
import com.csci5708.dalcommunity.fragment.ScannerFragment
import com.csci5708.dalcommunity.fragment.SearchFragment
import com.csci5708.dalcommunity.fragment.TimelineFragment
import com.csci5708.dalcommunity.fragment.TimeTableFragment
import com.csci5708.dalcommunity.model.User
import com.csci5708.dalcommunity.util.BroadcastQuestionsSharedValues
import com.example.dalcommunity.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeActivity : AppCompatActivity(), SearchFragment.OnUserSelectedListener {
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
        val userIcon = findViewById<ImageView>(R.id.user_icon)
        val petitionIcon = findViewById<ImageView>(R.id.petition_icon)
        val pokeIcon = findViewById<ImageView>(R.id.poke_icon)
        val interestsIcon = findViewById<ImageView>(R.id.common_interest_icon)
        val communityIcon = findViewById<ImageView>(R.id.community_icon)
        val lostFoundIcon = findViewById<ImageView>(R.id.lostFoundIcon)
        val userSearchIcon = findViewById<ImageView>(R.id.userSearchIcon)
        val scannerIcon = findViewById<ImageView>(R.id.scannerIcon)
        val broadcastIcon = findViewById<ImageView>(R.id.broadcast_question_icon)

        val bottomSheet = findViewById<FrameLayout>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val desiredHeight = calculateDesiredHeight()
        bottomSheet.layoutParams.height = desiredHeight
        bottomSheet.requestLayout()
        bottomSheetBehavior.peekHeight = desiredHeight / 2 // Set peek height as half of the dynamic height
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

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
            broadcastIcon.setImageResource(R.drawable.broadcast_outline)
            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, TimelineFragment())
                .addToBackStack(null)
                .commit()
        }


        timeTableIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table)
            //settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.home_fragment_container, TimeTableFragment(), "TIME_TABLE_FRAGMENT")
            transaction.addToBackStack(null)
            transaction.commit()
            findViewById<FrameLayout>(R.id.home_fragment_container).bringToFront()
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.outline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)

        }

        userIcon.setOnClickListener{
            val profileActivityIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileActivityIntent)
        }

        petitionIcon.setOnClickListener{
            val profileActivityIntent = Intent(this, PetitionActivity::class.java)
            startActivity(profileActivityIntent)
        }
        pokeIcon.setOnClickListener{
            val profileActivityIntent = Intent(this, PokeActivity::class.java)
            startActivity(profileActivityIntent)
        }

        interestsIcon.setOnClickListener{
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
            broadcastIcon.setImageResource(R.drawable.broadcast_outline)
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
            broadcastIcon.setImageResource(R.drawable.broadcast_outline)

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
            broadcastIcon.setImageResource(R.drawable.broadcast_outline)

            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, SearchFragment.newInstance(this, Bundle().apply {
                    putString("activity", "Home")
                }))
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
            broadcastIcon.setImageResource(R.drawable.broadcast_outline)

            val scannerFragment = ScannerFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, scannerFragment)
                .addToBackStack(null)
                .commit()
        }

        broadcastIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            interestsIcon.setImageResource(R.drawable.like_outline)
            communityIcon.setImageResource(R.drawable.groups_outline)
            broadcastIcon.setImageResource(R.drawable.broadcast)
            lostFoundIcon.setImageResource(R.drawable.baseline_content_paste_search_24)
            userSearchIcon.setImageResource(R.drawable.baseline_person_search_24)
            scannerIcon.setImageResource(R.drawable.scanner_outline)
            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, BroadcastQuestionFragment(fragmentManager))
                .commit()
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

    private fun calculateDesiredHeight(): Int {
        val iconHeight = dpToPx(24)
        val spacingBetweenIcons = dpToPx(8)
        val numberOfIconsInFirstRow = 5
        val totalIconsHeight = numberOfIconsInFirstRow * iconHeight
        val totalSpacingHeight = (11) * spacingBetweenIcons
        return totalIconsHeight + totalSpacingHeight
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onUserSelected(user: User) {
        Log.d("","")
    }
}