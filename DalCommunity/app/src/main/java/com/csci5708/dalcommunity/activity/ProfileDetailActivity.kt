package com.csci5708.dalcommunity.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.csci5708.dalcommunity.activity.PetitionActivity.Companion.REQUEST_IMAGE_CAPTURE
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

class ProfileDetailActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var profileImageView: ImageView
    private lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>

    private val interestsArray = arrayOf("None", "Sports", "Hiking", "Programming", "Arts")

    private lateinit var profileDetailName: TextView

    private lateinit var profileDetailEmail: TextView

    private lateinit var profileDetailDescription: TextView

    private lateinit var profileName: String

    private lateinit var profileEmail: String

    private lateinit var profileDescription: String

    private var profileImageUri: String = ""

    private var firstInterestSelected: String = "None"

    private var secondInterestSelected: String = "None"

    private var thirdInterestSelected: String = "None"

    private var isProfileImageSet = false

    private var storage = Firebase.storage

    private var storageRef = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val profileDetailToolbar: Toolbar = findViewById(R.id.profile_detail_toolbar)
        setSupportActionBar(profileDetailToolbar)
        supportActionBar?.title = "Edit Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        profileDetailName = findViewById(R.id.profile_detail_name_input)
        profileDetailEmail = findViewById(R.id.profile_detail_email_input)
        profileDetailDescription = findViewById(R.id.profile_detail_description_input)
        profileImageView = findViewById(R.id.profile_detail_image)

        setupProfileDetails()

        // Create an activity to launch the gallery to allow the user to select an image.
        imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                profileImageView.setImageURI(uri)
                isProfileImageSet = true
            }
        }

        val profileImageEditButton: ImageView = findViewById(R.id.profile_detail_image_edit_button)
        /* Logic for the popup window which allows users to select a profile image either from their
         * gallery or take a photo using their phone's camera */
        profileImageEditButton.setOnClickListener {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val profileImageEditPopupView =
                inflater.inflate(R.layout.profile_image_edit_popup_window, null)
            val popupWidth = ViewGroup.LayoutParams.WRAP_CONTENT
            val popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT
            val focusable = true
            val popupWindow =
                PopupWindow(profileImageEditPopupView, popupWidth, popupHeight, focusable)
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            val root = window.decorView.rootView as ViewGroup

            applyDim(root, 0.5f)
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
            profileImageEditPopupView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                    }

                    else -> {
                        // Clear dim effect and dismiss popup window on other touch events
                        clearDim(root)
                        popupWindow.dismiss()
                        true
                    }
                }
            }
            popupWindow.setOnDismissListener {
                clearDim(root)
            }
            val takePhotoButton: Button =
                profileImageEditPopupView.findViewById(R.id.profile_image_popup_take_photo_button)
            takePhotoButton.setOnClickListener {
                dispatchTakePictureIntent()
            }
            val chooseFromLibButton: Button =
                profileImageEditPopupView.findViewById((R.id.profile_image_popup_choose_from_lib_button))
            chooseFromLibButton.setOnClickListener {
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        val saveButton: Button = findViewById(R.id.profile_detail_save_button)
        saveButton.setOnClickListener {
            if (isProfileImageSet) {
                profileImageView.isDrawingCacheEnabled = true
                profileImageView.buildDrawingCache()
                // Get the latest profile image which has been set
                val profileBitmap = (profileImageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val profileImageRef =
                    storageRef.child("profile_image/" + Firebase.auth.currentUser?.email.toString() + ".jpg")
                val uploadTask = profileImageRef.putBytes(data)

                // Upload image to Firebase Cloud Storage
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    profileImageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    // Get the URI and set it to the Firebase user profile details
                    if (task.isSuccessful) {
                        profileImageUri = profileImageRef.downloadUrl.toString()
                        val userProfileUpdates = userProfileChangeRequest {
                            photoUri = Uri.parse(profileImageUri)
                        }
                        Firebase.auth.currentUser?.updateProfile(userProfileUpdates)
                    } else {
                        Toast.makeText(this, "Failed to upload profile image!", Toast.LENGTH_LONG)
                            .show()
                    }
                }.addOnSuccessListener { uri ->
                    // Get the URI and update the URI field in the Firestore user details collection
                    profileImageUri = uri.toString()
                    FireStoreSingleton.updateDataField(
                        "users",
                        Firebase.auth.currentUser?.email.toString(),
                        "photoUri",
                        profileImageUri
                    ) {
                        if (!it) {
                            Toast.makeText(
                                this,
                                "Failed to update profile image URI",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            val userDetail = User(
                profileDetailName.text.toString(),
                profileDetailEmail.text.toString(),
                profileDetailDescription.text.toString(),
                firstInterestSelected,
                secondInterestSelected,
                thirdInterestSelected,
                profileImageUri
            )

            val userDetailUpdateOnSuccess: (Boolean) -> Unit = { b: Boolean ->
                if (b)
                    Toast.makeText(this, "Successfully updated!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "Update failed! Please try again.", Toast.LENGTH_SHORT)
                        .show()
            }
            FireStoreSingleton.updateData(
                "users",
                Firebase.auth.currentUser?.email.toString(),
                userDetail,
                userDetailUpdateOnSuccess
            )
        }
    }

    /**
     * Get the user's profile details from Firestore
     */
    private fun setupProfileDetails() {
        FireStoreSingleton.getData(
            "users",
            Firebase.auth.currentUser?.email.toString(),
            { d: DocumentSnapshot -> getUserDataOnSuccess(d) },
            { getUserDataOnFailure() }
        )
    }

    /**
     * Callback function that is called fetching user details from Firestore has failed
     */
    private fun getUserDataOnFailure() {
        Toast.makeText(this, "Error fetching user details!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Callback function that is called when user details have been successfully fetched from Firestore
     *
     * @param doc user details document from the Firestore "user" collection
     */
    private fun getUserDataOnSuccess(doc: DocumentSnapshot) {
        val userDetails = doc.data
        profileDetailName.text = userDetails?.get("name").toString()
        profileDetailEmail.text = userDetails?.get("email").toString()
        profileDetailDescription.text = userDetails?.get("description").toString()
        firstInterestSelected = userDetails?.get("firstInterest").toString()
        secondInterestSelected = userDetails?.get("secondInterest").toString()
        thirdInterestSelected = userDetails?.get("thirdInterest").toString()
        profileName = userDetails?.get("name").toString()
        profileEmail = userDetails?.get("email").toString()
        profileDescription = userDetails?.get("description").toString()
        profileImageUri = userDetails?.get("photoUri").toString()

        /* Get the profile image */
        if (profileImageUri.isNotEmpty()) {
            val profileImageRef = storage.getReferenceFromUrl(profileImageUri)
            profileImageRef.getBytes(1024 * 1024).addOnSuccessListener { bytes ->
                val profileBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                profileImageView.setImageBitmap(profileBitmap)
            }
        }

        setupInterestSpinners()
    }

    /**
     * Setup and configure all the Spinner components in this layout
     */
    private fun setupInterestSpinners() {
        val firstSpinner: Spinner = findViewById(R.id.profile_detail_first_interest_spinner)
        val secondSpinner: Spinner = findViewById(R.id.profile_detail_second_interest_spinner)
        val thirdSpinner: Spinner = findViewById(R.id.profile_detail_third_interest_spinner)
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, interestsArray.toList())
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        firstSpinner.adapter = arrayAdapter
        secondSpinner.adapter = arrayAdapter
        thirdSpinner.adapter = arrayAdapter
        firstSpinner.onItemSelectedListener = this
        secondSpinner.onItemSelectedListener = this
        thirdSpinner.onItemSelectedListener = this
        firstSpinner.setSelection(arrayAdapter.getPosition(firstInterestSelected))
        secondSpinner.setSelection(arrayAdapter.getPosition(secondInterestSelected))
        thirdSpinner.setSelection(arrayAdapter.getPosition(thirdInterestSelected))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Dim the background when the popup window is created
     *
     * @param parent parent view that has to be dimmed
     * @param dimAmount alpha value of dimming
     */
    private fun applyDim(parent: ViewGroup, dimAmount: Float) {
        val dim: Drawable = ColorDrawable(Color.BLACK)
        dim.setBounds(0, 0, parent.width, parent.height)
        dim.alpha = (255 * dimAmount).toInt()
        val overlay: ViewGroupOverlay = parent.overlay
        overlay.add(dim)
    }

    /**
     * Remove the previously set dimming
     */
    private fun clearDim(parent: ViewGroup) {
        val overlay = parent.overlay
        overlay.clear()
    }

    /**
     * Function to start the camera to capture the profile photo
     */
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(this, "Unable to take photo!", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            val profileImage: Bitmap = data?.extras?.get("data") as Bitmap
            isProfileImageSet = true
            profileImageView.setImageBitmap(profileImage)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val interest = parent?.getItemAtPosition(position)

        // Get the item selected in each of the spinners created on this page
        when (parent?.id) {
            R.id.profile_detail_first_interest_spinner -> firstInterestSelected = interest as String
            R.id.profile_detail_second_interest_spinner -> secondInterestSelected =
                interest as String

            R.id.profile_detail_third_interest_spinner -> thirdInterestSelected = interest as String
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}