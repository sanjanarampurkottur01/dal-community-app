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
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
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
import android.widget.ProgressBar
import android.widget.RelativeLayout
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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream


/**
 * Fragment responsible for creating petitions.
 */
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
    var imgUrlForFireStore = ""

    private lateinit var dialog: Dialog



    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_petition, container, false)

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
    /**
     * Checks if any of the input fields (petition title or description) have been edited.
     *
     * @return true if any field has been edited, false otherwise.
     */
    private fun isFieldsEdited(): Boolean {
        val petitionTitle = petitionTitleEditText?.text.toString().trim()
        val petitionDesc = petitionDescEditText?.text.toString().trim()
        return petitionTitle.isNotEmpty() || petitionDesc.isNotEmpty()
    }
    /**
     * Displays a preview dialog showing the petition title, description, and optionally the image.
     */
    private fun showPreviewDialog() {
        val petitionTitle = petitionTitleEditText?.text.toString()
        val petitionDesc = petitionDescEditText?.text.toString()
        val imageUri = saveImageToContentProvider()

        val dialogView = layoutInflater.inflate(R.layout.petition_preview_dialog, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.preview_petition_title)
        val descTextView = dialogView.findViewById<TextView>(R.id.preview_petition_desc)
        val imageView = dialogView.findViewById<ImageView>(R.id.preview_petition_image)
        val progressBar = dialogView.findViewById<RelativeLayout>(R.id.progressBar)
        progressBar.visibility = View.GONE


        if (imageUri != null) {
            val bitmap = getBitmapFromUri(imageUri)
            bitmap?.let {
                imageView.setImageBitmap(bitmap)
                imageView.visibility = View.VISIBLE
                isImageInPetition = true
            }
        } else {
            imageView.visibility = View.GONE
        }

        communityGroupSpinner = dialogView.findViewById(R.id.linkGroup)
        fetchCommunityGroups()
        titleTextView.text = petitionTitle
        descTextView.text = petitionDesc
        dialog = Dialog(requireContext())
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
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
            progressBar.visibility = View.VISIBLE
            val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null) {
                uploadImageToFirebaseStorage(bitmap)
            } else {
                // No image selected, proceed with publishing without an image
                publishPetition(false, imageView)
                petitionTitleEditText?.setText("")
                petitionDescEditText?.setText("")
                petitionImage.setImageResource(R.drawable.upload_placeholder)
                if (imageUri != null) {
                    deleteImage(imageUri)
                }
            }
        }
        dialog.show()
    }

    /**
     * Uploads a bitmap image to Firebase Storage.
     *
     * @param bitmap The bitmap image to upload.
     */
    private fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        val storageReference = com.google.firebase.Firebase.storage.reference

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val timestamp: String = dateFormat.format(Date())
        val imageName = "petition_image_$timestamp.png"

        val imageRef = storageReference.child("petition_images/$imageName")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageData = baos.toByteArray()

        val uploadTask = imageRef.putBytes(imageData)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Log.d("UploadImage", "Image uploaded successfully. URL: $imageUrl")
                    imgUrlForFireStore = imageUrl
                    publishPetition(true, ImageView(context))
                    dialog.dismiss()
                    petitionTitleEditText?.setText("")
                    petitionDescEditText?.setText("")
                    petitionImage.setImageResource(R.drawable.upload_placeholder)
                }.addOnFailureListener { exception ->
                    Log.e("UploadImage", "Failed to retrieve image download URL: ${exception.message}")
                    Toast.makeText(context, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("UploadImage", "Image upload failed: ${task.exception?.message}")
                Toast.makeText(context, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Retrieves a Bitmap from the given URI.
     *
     * @param uri The URI of the image.
     * @return The Bitmap if successful, or null if the URI is invalid or the image cannot be decoded.
     */
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes the image associated with the given URI from the device's content resolver.
     *
     * @param imageUri The URI of the image to be deleted.
     */
    private fun deleteImage(imageUri: Uri) {
        requireActivity().contentResolver.delete(imageUri, null, null)
    }

    /**
     * Saves the user ID to SharedPreferences.
     *
     * @param userId The user ID to be saved.
     */
    private fun saveUserId(userId: String) {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }
    /**
     * Displays a dialog with options for taking a photo or choosing from the gallery.
     * Initiates corresponding actions based on user selection.
     */
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

    /**
     * Dispatches an intent to capture an image using the device's camera.
     */
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    /**
     * Dispatches an intent to pick an image from the device's gallery.
     */
    private fun dispatchPickImageIntent() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
    }

    /**
     * Handles the result of activities launched for capturing or picking images.
     * If the result is successful, it updates the petition image view accordingly.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode The result code returned by the child activity.
     * @param data An Intent that carries the result data.
     */
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
    /**
     * Saves the image displayed in the petition image view to the device's external content provider.
     *
     * @return The URI of the saved image if successful, null otherwise.
     */
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
    /**
     * Fetches all community groups from the Firestore database and populates the community group spinner with their names.
     */
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

    /**
     * Publishes a petition with the provided details to the Firestore database and notifies all users of the associated community group.
     *
     * @param isImageInPetition Boolean indicating whether an image is included in the petition.
     * @param imageView The ImageView containing the image for the petition.
     */
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
//        imgUrlForFireStore = "" //TODO: upload image to firestore, get the Url and store it in this variable, create a function to upload the image which is being displayed in the preview dialog box
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
                            var accessToken = ""
                            val SDK_INT = Build.VERSION.SDK_INT
                            if (SDK_INT > 8) {
                                val policy = StrictMode.ThreadPolicy.Builder()
                                    .permitAll().build()
                                StrictMode.setThreadPolicy(policy)
                                accessToken = FCMNotificationSender.getAccessToken(requireContext())
                            }
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
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(requireContext(), "Community group not found", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            },
            { exception ->
                Toast.makeText(requireContext(), "Failed to retrieve community group: ${exception.message}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        )
        imageView.setImageResource(R.drawable.upload_placeholder)
    }

    /**
     * Fetches Firebase Cloud Messaging (FCM) tokens for all users associated with a community group.
     *
     * @param communityGroupId The ID of the community group.
     * @return A list of FCM tokens associated with users in the community group.
     */
    private suspend fun fetchAllUsersEmail(communityGroupId: String): List<String> = coroutineScope {
        val db = FirebaseFirestore.getInstance()
        val fcmTokens = mutableListOf<String>()
        try {
            val communityGroupRef = db.collection("community-groups").document(communityGroupId)
            var userEmails: MutableList<String> = getAllEmails(communityGroupRef)
            val usersQuery = db.collection("users").whereIn("email", userEmails)
            val usersQuerySnapshot = usersQuery.get().await()
            val emailToFCMMap = usersQuerySnapshot.documents.associateBy({ it.id }, { it.getString("fcmToken") })
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

    /**
     * Retrieves all email addresses associated with users in a community group.
     *
     * @param communityGroupRef The reference to the document of the community group.
     * @return A list of email addresses of users in the community group.
     */
    private suspend fun getAllEmails(communityGroupRef: DocumentReference): MutableList<String> {
        val userEmails = mutableListOf<String>()
        try {
            val documentSnapshot = communityGroupRef.get().await()
            if (documentSnapshot.exists()) {
                val usersMap = documentSnapshot.data?.get("users") as? Map<String, Any>
                if (usersMap != null) {
                    val emails = usersMap.keys.toList()
                    userEmails.addAll(emails)
                } else {
                    println("Users map is null or empty")
                }
            } else {
                println("Document does not exist")
            }
        } catch (e: Exception) {
            println("Error getting document: $e")
        }
        return userEmails
    }
}
