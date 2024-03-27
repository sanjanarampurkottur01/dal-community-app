package com.csci5708.dalcommunity.firestore

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object FCMTokenManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firebaseMessaging = FirebaseMessaging.getInstance()

    fun updateOrStoreFCMToken(context: Context) {
        val currentUser = auth.currentUser
        firebaseMessaging.token.addOnCompleteListener { tokenTask ->
            if (!tokenTask.isSuccessful) {
                return@addOnCompleteListener
            }
            val fcmToken = tokenTask.result
            currentUser?.email?.let { userId ->
                val userDocRef = db.collection("users").document(userId)
                userDocRef.get().addOnCompleteListener { docTask ->
                    if (docTask.isSuccessful) {
                        val document: DocumentSnapshot? = docTask.result
                        if (document != null && document.exists()) {
                            userDocRef.update("fcmToken", fcmToken)
                                .addOnSuccessListener {
//                                    showToast(context, "FCM token updated successfully")
                                }
                                .addOnFailureListener { e ->
//                                    showToast(context, "Error updating FCM token: $e")
                                }
                        } else {
                            val userData = hashMapOf(
                                "fcmToken" to fcmToken
                            )
                            userDocRef.set(userData)
                                .addOnSuccessListener {
//                                    showToast(context, "FCM token stored successfully")
                                }
                                .addOnFailureListener { e ->
//                                    showToast(context, "Error storing FCM token: $e")
                                }
                        }
                    } else {
                        showToast(context, "Error getting user: ${docTask.exception}")
                    }
                }
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}