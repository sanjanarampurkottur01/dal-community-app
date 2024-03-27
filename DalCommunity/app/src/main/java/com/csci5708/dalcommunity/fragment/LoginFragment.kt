package com.csci5708.dalcommunity.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.csci5708.dalcommunity.activity.HomeActivity
import com.csci5708.dalcommunity.firestore.FCMTokenManager
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class LoginFragment : Fragment() {
    val SHARED_PREFERENCES = "sharedPref"
    val IS_SIGNED_IN = "isSignedIn"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = Firebase.auth

        val userEmail = view.findViewById<EditText>(R.id.editTextUsername)
        val password = view.findViewById<EditText>(R.id.editTextPassword)
        val loginBtn = view.findViewById<Button>(R.id.btn_login)

        loginBtn.setOnClickListener {
            if (!(userEmail.text.toString().isEmpty() || password.text.toString().isEmpty())) {
                auth.signInWithEmailAndPassword(userEmail.text.toString(), password.text.toString())
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            FCMTokenManager.updateOrStoreFCMToken(requireContext())
                            Toast.makeText(
                                requireContext(),
                                "Welcome!",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit().putBoolean(IS_SIGNED_IN, true).apply()
                            FCMTokenManager.updateOrStoreFCMToken(requireActivity())
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Authentication failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please do not leave fields empty.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
