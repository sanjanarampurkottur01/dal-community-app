package com.csci5708.dalcommunity.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.csci5708.dalcommunity.adapter.QuestionsAdapter
import com.csci5708.dalcommunity.firestore.FCMNotificationSender
import com.csci5708.dalcommunity.util.BroadcastQuestionsSharedValues
import com.csci5708.dalcommunity.util.DateUtils.Companion.getDateTimeStamp
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BroadcastQuestionFragment(fragmentManager: FragmentManager) : Fragment() {

    val fragmentManagerLocal = fragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_broadcast_question, container, false)
        val questionET = view.findViewById<EditText>(R.id.questionET)
        val broadcastBtn = view.findViewById<Button>(R.id.broadcastButton)
        BroadcastQuestionsSharedValues.fetchQuestions()
        view.findViewById<RelativeLayout>(R.id.responsesBtn).setOnClickListener{
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(resources.getDrawable(R.drawable.dialog_background))
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.see_responses_dialog)
            val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
            val emptyAnimation = dialog.findViewById<LottieAnimationView>(R.id.emptyAnimation)

            if (BroadcastQuestionsSharedValues.questions.isEmpty()) {
                emptyAnimation.visibility = View.VISIBLE
            } else {
                emptyAnimation.visibility = View.GONE
                recyclerView.layoutManager = LinearLayoutManager(context)
                val questions = BroadcastQuestionsSharedValues.questions
                val adapter = QuestionsAdapter(requireActivity(), questions)
                recyclerView.adapter = adapter
            }
            dialog.show()
        }
        broadcastBtn.setOnClickListener{
            if (questionET.text.toString().isBlank()) {
                Toast.makeText(context, "Please enter your query in the provided space.", Toast.LENGTH_SHORT).show()
            } else {
                broadcastBtn.alpha = 0.5F
                broadcastBtn.text = "Hold on tight.."
                val lastQuestionBroadcastOn = getLastQuestionBroadcastOn(requireActivity())

                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())
//                if (lastQuestionBroadcastOn == currentDate) {
//                    Toast.makeText(context, "You can broadcast again tomorrow!", Toast.LENGTH_SHORT).show()
//                } else {
//                    saveLastQuestionBroadcastOn(requireActivity(), currentDate)
                    sendMessage(questionET.text.toString())
//                }
            }
        }
        return view
    }

    fun sendMessage(message: String) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        val broadcastId = getDateTimeStamp()
        var messageToSend = "¨${userEmail}∫${broadcastId}µ$message"
        val firestore = FirebaseFirestore.getInstance()

        val userEmails = mutableListOf<String>()
        val receiverTokens = mutableListOf<String>()

        runBlocking{
            val usersCollectionRef = firestore.collection("users")

            usersCollectionRef
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val email = document.getString("email")
                        email?.let {
                            userEmails.add(it)
                        }
                        if (!email.contentEquals(FirebaseAuth.getInstance().currentUser?.email.toString())) {
                            val token = document.getString("fcmToken")
                            token?.let {
                                receiverTokens.add(it)
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("", "Error getting user emails", exception)
                }
        }

        firestore.runTransaction { transaction ->

            var accessToken = ""
            val SDK_INT = Build.VERSION.SDK_INT
            if (SDK_INT > 8) {
                val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                accessToken = FCMNotificationSender.getAccessToken(requireActivity())
            }
            activity?.let {
                FCMNotificationSender.sendNotificationToMultipleUsers(
                    targetTokens = receiverTokens,
                    title = "Help a peer out!",
                    message = messageToSend,
                    context = it,
                    accessToken = accessToken,
                    "high"
                )
            }

            val broadcastDocumentRef = Firebase.firestore.collection("broadcastQuestions").document(FirebaseAuth.getInstance().currentUser?.email.toString())
            val map = mutableMapOf<String, Map<String, String>>()
            map[broadcastId] = mapOf("broadcastId" to broadcastId, "message" to message)

            broadcastDocumentRef.set(map, SetOptions.merge())

            true // Return true to commit the transaction
        }.addOnSuccessListener {
            Toast.makeText(context, "Your question has been broadcast. Keep checking for people's responses!", Toast.LENGTH_SHORT).show()
            fragmentManagerLocal.beginTransaction()
                .replace(R.id.home_fragment_container, TimelineFragment())
                .commit()
        }.addOnFailureListener {
            Toast.makeText(context, "Unable to broadcast your question!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLastQuestionBroadcastOn(context: Activity): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("DATES", Context.MODE_PRIVATE)
        return sharedPreferences.getString("lastQuestionBroadcastOn", "0") ?: "0"
    }

    private fun saveLastQuestionBroadcastOn(context: Activity, date: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("DATES", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("lastQuestionBroadcastOn", date)
        editor.apply()
    }
}