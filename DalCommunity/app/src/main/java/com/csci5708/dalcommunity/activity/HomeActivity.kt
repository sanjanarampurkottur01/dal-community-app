package com.csci5708.dalcommunity.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.csci5708.dalcommunity.fragment.CommentFragment
import com.example.dalcommunity.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class HomeActivity : AppCompatActivity(), HomeAdapter.onCommentClickListener {
    val SHARED_PREFERENCES = "sharedPref"
    val IS_SIGNED_IN = "isSignedIn"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(IS_SIGNED_IN, false)) {
            showLoginDialog()
        }


        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val posts = listOf("", "", "")

        var adapter = HomeAdapter(this, posts)
        adapter.setOnCommentClickListener(this)
        recyclerView.adapter = adapter

        val homeIcon = findViewById<ImageView>(R.id.home_icon)
        val timeTableIcon = findViewById<ImageView>(R.id.time_table_icon)
        val settingsIcon = findViewById<ImageView>(R.id.settings_icon)
        val userIcon = findViewById<ImageView>(R.id.user_icon)

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
        }

        timeTableIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table)
            settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user_outline)
        }

        settingsIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            settingsIcon.setImageResource(R.drawable.settings)
            userIcon.setImageResource(R.drawable.user_outline)
        }

        userIcon.setOnClickListener{
            homeIcon.setImageResource(R.drawable.home_outline)
            timeTableIcon.setImageResource(R.drawable.time_table_outline)
            settingsIcon.setImageResource(R.drawable.settings_outline)
            userIcon.setImageResource(R.drawable.user)
        }
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
                            Toast.makeText(
                                baseContext,
                                "Welcome!",
                                Toast.LENGTH_SHORT,
                            ).show()
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

    override fun onCommentClick(position: Int) {
        Toast.makeText(this,"test",Toast.LENGTH_LONG).show()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_top_comment, R.anim.slide_out_down_comment)
        fragmentTransaction.replace(R.id.fragment_container, CommentFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}