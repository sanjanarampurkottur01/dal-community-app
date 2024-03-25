package com.csci5708.dalcommunity.fragment

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.csci5708.dalcommunity.firestore.FCMNotificationSender
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking


class CreatePetitionFragment : Fragment() {

    private var petitionTitleEditText: EditText? = null
    private var petitionDescEditText: EditText? = null
    private lateinit var petitionImage: ImageView
    private val PREF_NAME = "user_details"
    private val KEY_USER_ID = "user_id"
    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }
    private lateinit var communityGroupSpinner: Spinner
    private var isImageInPetition: Boolean =  false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_petition, container, false)

        // Find EditTexts
        petitionTitleEditText = view.findViewById(R.id.pokeToUserMessage)
        petitionDescEditText = view.findViewById(R.id.petitionDesc)

        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            saveUserId(userId)
        }

        petitionImage = view.findViewById(R.id.petitionImage)
        petitionImage.setOnClickListener {
            showOptions()
        }
        val previewButton = view.findViewById<View>(R.id.previewBtn)
        previewButton.setOnClickListener {
            if (isFieldsEdited()) {
                showPreviewDialog()
            } else {
                Toast.makeText(requireContext(), "Please edit the fields first.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    private fun isFieldsEdited(): Boolean {
        val petitionTitle = petitionTitleEditText?.text.toString().trim()
        val petitionDesc = petitionDescEditText?.text.toString().trim()
        return petitionTitle.isNotEmpty() || petitionDesc.isNotEmpty()
    }
    private fun showPreviewDialog() {
        val petitionTitle = petitionTitleEditText?.text.toString()
        val petitionDesc = petitionDescEditText?.text.toString()
        val imageUri = saveImageToContentProvider()

        val dialogView = layoutInflater.inflate(R.layout.petition_preview_dialog, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.preview_petition_title)
        val descTextView = dialogView.findViewById<TextView>(R.id.preview_petition_desc)
        val imageView = dialogView.findViewById<ImageView>(R.id.preview_petition_image)

        if (imageUri != null) {
            imageView.setImageURI(imageUri)
            imageView.visibility = View.VISIBLE // Show the ImageView
            isImageInPetition = true
        } else {
            imageView.visibility = View.GONE // Hide the ImageView
        }

        communityGroupSpinner = dialogView.findViewById(R.id.linkGroup)
        fetchCommunityGroups()
        titleTextView.text = petitionTitle
        descTextView.text = petitionDesc
        imageUri?.let { uri ->
            val bitmap = getBitmapFromUri(uri)
            bitmap?.let {
                imageView.setImageBitmap(bitmap)
            }
        }
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt() // Adjust 0.9 to your desired percentage
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = width
        dialog.window?.attributes = layoutParams


        val discardButton = dialog.findViewById<Button>(R.id.discardBtn)
        discardButton.setOnClickListener {
            dialog.dismiss()
            if (imageUri != null) {
                deleteImage(imageUri)
            }
        }
        val publishButton = dialog.findViewById<Button>(R.id.publishBtn)
        publishButton.setOnClickListener {
            publishPetition(isImageInPetition, imageView)
            petitionTitleEditText?.setText("")
            petitionDescEditText?.setText("")
            dialog.dismiss()
            if (imageUri != null) {
                deleteImage(imageUri)
            }
        }
        dialog.show()
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
    private fun deleteImage(imageUri: Uri) {
        requireActivity().contentResolver.delete(imageUri, null, null)
    }

    private fun saveUserId(userId: String) {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    private fun showOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> dispatchPickImageIntent()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun dispatchPickImageIntent() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    petitionImage.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImage = data?.data
                    petitionImage.setImageURI(selectedImage)
                }
            }
        }
    }

    private fun saveImageToContentProvider(): Uri? {
        val drawable = petitionImage.drawable
        if (drawable !is BitmapDrawable) {
            return null
        }
        val bitmap = (petitionImage.drawable as? BitmapDrawable)?.bitmap ?: return null
        val resolver = requireActivity().contentResolver
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val timestamp: String = dateFormat.format(Date())
        val imageName = "petition_image_$timestamp.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
        val contentUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        contentUri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
        return contentUri
    }

    private fun fetchCommunityGroups() {
        FireStoreSingleton.getAllDocumentsOfCollection("community-groups",
            { documents ->
                val communityGroups = mutableListOf<String>()
                for (document in documents) {
                    val groupName = document.getString("name")
                    groupName?.let { communityGroups.add(it) }
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, communityGroups)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                communityGroupSpinner.adapter = adapter
            },
            { exception ->
                Toast.makeText(requireContext(), "Failed to load community groups: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun publishPetition(isImageInPetition: Boolean, imageView: ImageView) {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val petitionTitle = petitionTitleEditText?.text.toString()
        val petitionDesc = petitionDescEditText?.text.toString()
        val communityGroup = communityGroupSpinner.selectedItem.toString()

        if (currentUser != null) {
            if (currentUser.email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                return
            }
        }
        var imgUrlForFireStore = "" //TODO: upload image to imgBBor firestore storePetitionImage(imageView)
        FireStoreSingleton.get("community-groups", "name", communityGroup,
            { documents ->
                if (documents.isNotEmpty()) {
                    val communityGroupId = documents[0].id
                    var fcmTokens:  List<String>;
                    runBlocking {
                        fcmTokens = fetchAllUsersEmail(communityGroupId)
                    }

                    Log.d("userEmail", fcmTokens.toString())
                    val petitionData = hashMapOf(
                        "title" to petitionTitle,
                        "description" to petitionDesc,
                        "creation_date" to Timestamp.now(),
                        "number_signed" to 0,
                        "community" to communityGroupId,
                        "user" to (currentUser?.email ?: ""),
                        "imgUrl" to imgUrlForFireStore,
                        "signed_user" to ArrayList<String>()
                    )
                    FireStoreSingleton.addData("petitions", petitionData) { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "Petition published successfully!", Toast.LENGTH_SHORT).show()
                            Toast.makeText(requireContext(),
                                "Notifying all users of $communityGroup", Toast.LENGTH_SHORT).show()
                            var accessToken = FCMNotificationSender.getAccessToken(requireContext())
                            FCMNotificationSender.sendNotificationToMultipleUsers(
                                targetTokens = fcmTokens,
                                title = petitionTitle,
                                message = "$communityGroup has recently published a Petition",
                                context = requireContext(),
                                accessToken = accessToken,
                                "high"
                            )

                        } else {
                            Toast.makeText(requireContext(), "Failed to publish petition", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Community group not found", Toast.LENGTH_SHORT).show()
                }
            },
            { exception ->
                Toast.makeText(requireContext(), "Failed to retrieve community group: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private suspend fun fetchAllUsersEmail(communityGroupId: String): List<String> = coroutineScope {
        val db = FirebaseFirestore.getInstance()
        val fcmTokens = mutableListOf<String>()
        try {
            val communityGroupRef = db.collection("community-groups").document(communityGroupId)
            val usersSnapshot = communityGroupRef.collection("users").get().await()
            val userEmails = usersSnapshot.documents.mapNotNull { it.id } // Extract email IDs

            // Fetch all user documents at once
            val usersQuery = db.collection("users").whereIn("email", userEmails)
            val usersQuerySnapshot = usersQuery.get().await()

            // Map email to FCM token
            val emailToFCMMap = usersQuerySnapshot.documents.associateBy({ it.id }, { it.getString("fcmToken") })

            // Populate fcmTokens list
            for (email in userEmails) {
                val fcmToken = emailToFCMMap[email]
                if (!fcmToken.isNullOrBlank()) {
                    fcmTokens.add(fcmToken)
                }
            }
        } catch (e: Exception) {
            println("Error fetching users' FCM tokens: ${e.message}")
        }
        return@coroutineScope fcmTokens
    }
}
