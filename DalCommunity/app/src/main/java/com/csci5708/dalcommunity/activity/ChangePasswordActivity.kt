package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.csci5708.dalcommunity.constants.AppConstants
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

/**
 * Activity for changing the user password.
 * This activity allows users to change their password and handles the process using Firebase authentication.
 */
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var passwordInput1: TextView
    private lateinit var passwordInput2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setting up the toolbar
        val changePasswdToolbar: Toolbar = findViewById(R.id.change_password_toolbar)
        setSupportActionBar(changePasswdToolbar)
        supportActionBar?.title = "Change Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Initialize password input fields
        passwordInput1 = findViewById(R.id.change_password_input_1)
        passwordInput2 = findViewById(R.id.change_password_input_2)

        val changePasswordButton: Button = findViewById(R.id.change_password_button)
        changePasswordButton.setOnClickListener {
            val pass1: String = passwordInput1.text.toString()
            val pass2: String = passwordInput2.text.toString()

            if (pass1 != pass2) {
                // Show toast message if passwords do not match
                Toast.makeText(
                    this,
                    "Passwords do not match! Please enter again.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val currentUser = Firebase.auth.currentUser
                /* Update the password of the current user using Firebase.auth.
                 * After successful update, log out the user start the LoginAndSignUpActivity
                 */
                currentUser!!.updatePassword(pass1).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val sharedPreferences =
                            getSharedPreferences(AppConstants.APP_SHARED_PREFERENCES, MODE_PRIVATE)
                        Toast.makeText(this, "Password updated!", Toast.LENGTH_LONG).show()
                        if (sharedPreferences != null) {
                            Firebase.auth.signOut()
                            sharedPreferences.edit()
                                .putBoolean(AppConstants.SP_IS_SIGNED_IN_KEY, false)
                                .apply()
                        }
                        val intent = Intent(this, LoginSignUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Password update failed! Please re-login and try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}