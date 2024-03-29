package com.csci5708.dalcommunity.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.csci5708.dalcommunity.activity.HomeActivity
import com.csci5708.dalcommunity.firestore.FCMTokenManager
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
/**
 * Fragment for user sign up functionality.
 */
class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    val SHARED_PREFERENCES = "sharedPref"
    val IS_SIGNED_IN = "isSignedIn"
    private val interestsArray = arrayOf("Sports", "Hiking", "Programming", "Arts")
    private var firstInterestSelected: String = "Sports"
    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }
    /**
     * Initializes views and sets up listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = Firebase.auth

        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        val emailEditText = view.findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.editTextConfirmPassword)
        val signUpButton = view.findViewById<Button>(R.id.btn_login)
        val firstSpinner: Spinner = view.findViewById(R.id.profile_detail_first_interest_spinner_on_signup)
        val arrayAdapter = object : ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            interestsArray.toList()
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
                return view
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        firstSpinner.adapter = arrayAdapter
        firstSpinner.setSelection(arrayAdapter.getPosition(firstInterestSelected))

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val firstInterest = firstSpinner.selectedItem.toString()


            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val userEmail = currentUser?.email
                        if (userEmail != null) {
                            createUserEntryInFirestore(name, userEmail, firstInterest)
                        }
                        Toast.makeText(
                            requireContext(),
                            "User Creation Successful.",
                            Toast.LENGTH_SHORT
                        ).show()
                        FCMTokenManager.updateOrStoreFCMToken(requireActivity())
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { signInTask ->
                                if (signInTask.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "You have successfully Logged In!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    requireActivity().getSharedPreferences(
                                        SHARED_PREFERENCES,
                                        AppCompatActivity.MODE_PRIVATE
                                    ).edit().putBoolean(IS_SIGNED_IN, true).apply()
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
                            "User Creation failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
    /**
     * Creates a new user entry in Firestore.
     */
    private fun createUserEntryInFirestore(name: String, email: String, firstInterest: String) {
        val userDetails = User(
            name,
            email,
            "",
            firstInterest,
            "",
            "",
            ""
        )
        val onComplete = { b: Boolean ->
            if (b) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                Firebase.auth.currentUser?.updateProfile(profileUpdates)
                Toast.makeText(requireContext(), "User created in Firestore!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Failed to create user in Firestore!", Toast.LENGTH_LONG).show()
            }
        }
        FireStoreSingleton.addData(
            "users",
            email, // Use email as the document ID
            userDetails,
            onComplete
        )
    }

}
