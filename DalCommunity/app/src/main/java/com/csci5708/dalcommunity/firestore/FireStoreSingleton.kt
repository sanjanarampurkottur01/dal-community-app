package com.csci5708.dalcommunity.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

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
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

    fun addData(
        collection: String,
        data: Any,
        onComplete: (DocumentReference) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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

    fun getData(
        collection: String,
        document: String,
        onSuccess: (DocumentSnapshot) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fireStoreInstance.collection(collection)
            .document(document)
            .get()
            .addOnSuccessListener { onSuccess(it) }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateDataField(
        collection: String,
        document: String,
        fieldName: String,
        value: Any,
        onComplete: (Boolean) -> Unit
    ) {
        fireStoreInstance.collection(collection).document(document).update(fieldName, value)
            .addOnSuccessListener { onComplete(true) }.addOnFailureListener { onComplete(false) }
    }

    fun get(
        collection: String,
        field: String,
        value: Any,
        onSuccess: (List<DocumentSnapshot>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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

    fun getAllDocumentsOfCollection(
        collection: String,
        onSuccess: (List<DocumentSnapshot>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fireStoreInstance.collection(collection)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.documents)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getAllDocumentsOfCollectionWhereEqualTo(
        collection: String,
        fieldName: String,
        value: Any,
        onSuccess: (List<DocumentSnapshot>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fireStoreInstance.collection(collection).whereEqualTo(fieldName, value).get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.documents)
            }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun getDataRealTime(
        collection: String,
        document: String,
        listener: EventListener<QuerySnapshot>, // Change the listener type to QuerySnapshot
        onFailure: (Exception) -> Unit
    ) {
        val collectionRef = fireStoreInstance.collection("$collection/$document/messages")
        collectionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onFailure(e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                listener.onEvent(snapshot, null)
            }
        }
    }
}