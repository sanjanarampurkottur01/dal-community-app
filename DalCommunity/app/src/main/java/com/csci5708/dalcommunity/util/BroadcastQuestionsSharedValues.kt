package com.csci5708.dalcommunity.util

import com.csci5708.dalcommunity.model.Question
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

object BroadcastQuestionsSharedValues {
    var questions : ArrayList<Question> = arrayListOf()

    fun fetchQuestions() {
        questions.clear()
        val broadcastDocumentRef = Firebase.firestore.collection("broadcastQuestions").document(
            FirebaseAuth.getInstance().currentUser?.email.toString())
        broadcastDocumentRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val data = document.data
                if (data != null) {
                    var localId = ""
                    var localQuestion = ""
                    var localAnswer = arrayListOf<String>()
                    for ((key, value) in data) {
                        localAnswer = arrayListOf()
                        localQuestion = ""
                        localId = ""
                        val map = value as Map<String, *>
                        localId = map["broadcastId"].toString()
                        localQuestion = map["message"].toString()
                        if (map.keys.contains("answers")) {
                            localAnswer = map["answers"] as ArrayList<String>
                        }
                        questions.add(Question(localId, localQuestion, localAnswer))
                    }
                } else {
                    println("Document data is null.")
                }
            } else {
                println("No such document!")
            }
        }.addOnFailureListener { exception ->
            println("Error getting document: $exception")
        }
    }
}