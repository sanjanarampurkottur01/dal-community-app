package com.csci5708.dalcommunity.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dalcommunity.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class PreviewPetitionActivity : AppCompatActivity() {
    private lateinit var communityGroupSpinner: Spinner
    private lateinit var publishButton: Button
    private lateinit var petitionTitleTextView: TextView
    private lateinit var petitionDescTextView: TextView
    private val PREF_NAME = "user_details"

    // Define the key for saving userId
    private val KEY_USER_ID = "user_id"

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview_petition)
        petitionTitleTextView= findViewById(R.id.preview_petition_title)
        petitionDescTextView = findViewById(R.id.preview_petition_desc)
        val petitionImageView: ImageView = findViewById(R.id.preview_petition_image)

        val extras = intent.extras
        if (extras != null) {
            val petitionTitle = extras.getString("PETITION_TITLE")
            val petitionDesc = extras.getString("PETITION_DESC")
            val imageUriString = extras.getString("PETITION_IMAGE_URI")
            val imageUri = Uri.parse(imageUriString)

            petitionTitleTextView.text = petitionTitle
            petitionDescTextView.text = petitionDesc
            petitionImageView.setImageURI(imageUri)
        }
        communityGroupSpinner = findViewById(R.id.linkGroup)
        publishButton = findViewById(R.id.publishBtn)
        fetchCommunityGroups()
        publishButton.setOnClickListener {
            publishPetition()
        }
    }
    private fun fetchCommunityGroups() {
        firestore.collection("community-groups")
            .get()
            .addOnSuccessListener(OnSuccessListener { documents ->
                val communityGroups = mutableListOf<String>()
                for (document in documents) {
                    val groupName = document.getString("name")
                    groupName?.let { communityGroups.add(it) }
                }
                // Populate the spinner with community groups
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, communityGroups)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                communityGroupSpinner.adapter = adapter
            })
            .addOnFailureListener(OnFailureListener { exception ->
                Toast.makeText(this, "Failed to load community groups: ${exception.message}", Toast.LENGTH_SHORT).show()

            })
    }
    private fun publishPetition() {
        val petitionTitle = petitionTitleTextView.text.toString()
        val petitionDesc = petitionDescTextView.text.toString()
        val communityGroup = communityGroupSpinner.selectedItem.toString()
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString(KEY_USER_ID, "")
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the community group document ID
        firestore.collection("community-groups")
            .whereEqualTo("name", communityGroup)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    val communityGroupId = documents.documents[0].id
                    // Add the petition record with community group reference
                    val petitionData = hashMapOf(
                        "title" to petitionTitle,
                        "description" to petitionDesc,
                        "community-group" to firestore.document("community-groups/$communityGroupId"),
                        "user" to firestore.document("users/$userId")
                    )

                    firestore.collection("petitions")
                        .add(petitionData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Petition published successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, PetitionActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed to publish petition: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Community group not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to publish petition: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
