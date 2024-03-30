package com.csci5708.dalcommunity.firestore

import com.csci5708.dalcommunity.model.SavedPostGroup
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * Singleton class that provides APIs to the application to interact with FireStore
 */
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

    fun getSavedPostGroupsForUser(id: String, onSuccess: (List<SavedPostGroup>) -> Unit, onFailure: (Exception) -> Unit) {
        fireStoreInstance.collection("savedPosts")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedPostGroups = mutableListOf<SavedPostGroup>()

                    val data = document.data
                    data?.forEach { (groupName, groupData) ->
                        if (groupData is Map<*, *>) {
                            val name = groupData["name"] as? String ?: ""
                            val posts = (groupData["posts"] as? List<*>)?.filterIsInstance<String>()
                                ?.toTypedArray()
                                ?: emptyArray()
                            val savedPostGroup = SavedPostGroup(name, posts)
                            savedPostGroups.add(savedPostGroup)
                        }
                    }

                    onSuccess(savedPostGroups)
                } else {
                    onFailure(Exception("Document not found"))
                }
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

    fun getChatsData(
        collection: String,
        document: String,
        listener: EventListener<QuerySnapshot>,
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