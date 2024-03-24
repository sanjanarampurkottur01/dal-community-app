package com.csci5708.dalcommunity.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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

    fun addData(collection: String, data: Any, onComplete: (DocumentReference) -> Unit, onFailure: (Exception) -> Unit) {
        fireStoreInstance.collection(collection)
            .add(data)
            .addOnSuccessListener {
                onComplete(it)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun addData(collection: String, document: String, data: Any, onComplete: (Boolean) -> Unit) {
        fireStoreInstance.collection(collection)
            .document(document)
            .set(data).addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun get(collection: String, field: String, value: Any, onSuccess: (List<DocumentSnapshot>) -> Unit, onFailure: (Exception) -> Unit) {
        fireStoreInstance.collection(collection)
            .whereEqualTo(field, value)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.documents)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun getAllDocumentsOfCollection(collection: String, onSuccess: (List<DocumentSnapshot>) -> Unit, onFailure: (Exception) -> Unit) {
        fireStoreInstance.collection(collection)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.documents)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun getFireStoreInstanceSingleton(): FirebaseFirestore{
        return fireStoreInstance
    }
}