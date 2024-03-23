package com.csci5708.dalcommunity.firestore

import com.google.firebase.firestore.FirebaseFirestore

object FireStoreSingleton {
    private val fireStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Function to add data to a FireStore collection
    fun addData(collection: String, data: Any, onComplete: (Boolean) -> Unit) {
        fireStoreInstance.collection(collection)
            .add(data)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

    fun deleteData(collection: String, id: String, onComplete: (Boolean) -> Unit) {
        fireStoreInstance.collection(collection).document(id)
            .delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

    fun updateData(collection: String, id: String, data: Any, onComplete: (Boolean) -> Unit) {
        fireStoreInstance.collection(collection).document(id)
            .set(data)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener{ e ->
                onComplete(false)
            }
    }
}