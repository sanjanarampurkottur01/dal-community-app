package com.csci5708.dalcommunity.activity

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Contacts
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.csci5708.dalcommunity.firestore.FCMTokenManager
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.fragment.CommentFragment
import com.csci5708.dalcommunity.fragment.TimelineFragment
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class HomeActivity : AppCompatActivity() {
    val SHARED_PREFERENCES = "sharedPref"
    val IS_SIGNED_IN = "isSignedIn"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askToEnableNotificationsIfNeeded(this)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(IS_SIGNED_IN, false)) {
            showLoginDialog()
        }

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
        val settingsIcon = findViewById<ImageView>(R.id.settings_icon)
        val userIcon = findViewById<ImageView>(R.id.user_icon)
        val petitionIcon = findViewById<ImageView>(R.id.petition_icon)
        val pokeIcon = findViewById<ImageView>(R.id.poke_icon)

        val bottomSheet = findViewById<FrameLayout>(R.id.bottom_sheet)
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 300
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        homeIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            pokeIcon.setImageResource(R.drawable.poke_outline)

        }


        timeTableIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table)
            settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            pokeIcon.setImageResource(R.drawable.poke_outline)
        }

        settingsIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            settingsIcon.setImageResource(R.drawable.settings)
            userIcon.setImageResource(R.drawable.user_outline)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            pokeIcon.setImageResource(R.drawable.poke_outline)
        }

        userIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
            petitionIcon.setImageResource(R.drawable.petition_outline)
            val profileActivityIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileActivityIntent)
        }

        petitionIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_outline)
            val profileActivityIntent = Intent(this, PetitionActivity::class.java)
            startActivity(profileActivityIntent)
        }
        pokeIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
            petitionIcon.setImageResource(R.drawable.petition_filled)
            pokeIcon.setImageResource(R.drawable.poke_filled)
            val profileActivityIntent = Intent(this, PokeActivity::class.java)
            startActivity(profileActivityIntent)
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

    private fun showLoginDialog() {
        val auth = Firebase.auth
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.sign_in_dialog)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val loginBtn = dialog.findViewById<View>(R.id.buttonLogin) as Button
        val userEmail = dialog.findViewById<View>(R.id.editTextUsername) as EditText
        val password = dialog.findViewById<View>(R.id.editTextPassword) as EditText
        loginBtn.setOnClickListener {
            if (!(userEmail.text.toString().isEmpty() || password.text.toString().isEmpty())) {
                auth.signInWithEmailAndPassword(userEmail.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            FCMTokenManager.updateOrStoreFCMToken(this)

                            Toast.makeText(
                                baseContext,
                                "Welcome!",
                                Toast.LENGTH_SHORT,
                            ).show()
                            getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit().putBoolean(IS_SIGNED_IN, true).apply()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Authentication failed!",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Please do not leave fields empty.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        val signUpTV = dialog.findViewById<View>(R.id.signUpTV) as TextView
        signUpTV.setOnClickListener {
            dialog.dismiss()
            val signUpDialog = Dialog(this)
            signUpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signUpDialog.setCancelable(false)
            signUpDialog.setContentView(R.layout.sign_up_dialog)
            signUpDialog.setCancelable(false)
            signUpDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            signUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val userEmailSignUp = signUpDialog.findViewById<View>(R.id.editTextUsername) as EditText
            val passwordSignUp = signUpDialog.findViewById<View>(R.id.editTextPassword) as EditText
            signUpDialog.findViewById<Button>(R.id.buttonSignUp).setOnClickListener {
                if (!(userEmailSignUp.text.toString().isEmpty() || passwordSignUp.text.toString().isEmpty())) {
                    auth.createUserWithEmailAndPassword(
                        userEmailSignUp.text.toString(),
                        passwordSignUp.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    baseContext,
                                    "User Creation Successful.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                /* Create a document with user ID = emailID in users collection in Firestore */
                                createUserEntryInFirestore()
                                auth.signInWithEmailAndPassword(
                                    userEmailSignUp.text.toString(),
                                    passwordSignUp.text.toString()
                                )
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                baseContext,
                                                "You have successfully Logged In!",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit().putBoolean(IS_SIGNED_IN, true).apply()
                                            dialog.dismiss()
                                        } else {
                                            Toast.makeText(
                                                baseContext,
                                                "Authentication failed!",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "User Creation failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Please do not leave fields empty.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

            signUpDialog.findViewById<TextView>(R.id.loginTV).setOnClickListener {
                signUpDialog.dismiss()
                showLoginDialog()
            }
            signUpDialog.show()
        }
        dialog.show()
    }

    private fun createUserEntryInFirestore() {
        val currentUser = Firebase.auth.currentUser!!
        val userDetails = User(
            "",
            currentUser.email!!,
            "",
            "",
            "",
            "",
            ""
        )
        val onComplete = { b: Boolean ->
            if (b) {
                Toast.makeText(this, "User created in Firestore!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to create user in Firestore!", Toast.LENGTH_LONG).show()
            }
        }
        FireStoreSingleton.addData(
            "users",
            userDetails.email,
            userDetails,
            onComplete
        )
    }
}