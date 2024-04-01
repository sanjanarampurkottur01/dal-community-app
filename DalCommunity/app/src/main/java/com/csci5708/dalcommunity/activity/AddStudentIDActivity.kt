package com.csci5708.dalcommunity.activity

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.csci5708.dalcommunity.constants.AppConstants
import com.example.dalcommunity.R

/**
 * Activity for adding student ID photo
 */

class AddStudentIDActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var studentIDPhoto: ImageView
    private var currentPhotoUri: Uri? = null
    private var hasImage = false
    private val REQUEST_IMAGE_CAPTURE = 1

    // Activity result launcher for cropping image
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            uriContent?.let { uri -> handleCropResult(uri) }
        } else {
            val exception = result.error
        }
    }

    // Initialize activity components and retrieve saved photo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student_id)

        sharedPreferences = getSharedPreferences(AppConstants.APP_SHARED_PREFERENCES, MODE_PRIVATE)
        studentIDPhoto = findViewById(R.id.student_id_photo)
        val capturePhotoButton: Button = findViewById(R.id.btn_capture_photo)
        val updatePhotoButton: Button = findViewById(R.id.btn_update_photo)
        val deletePhotoButton: Button = findViewById(R.id.btn_delete_photo)

        val profileDetailToolbar: Toolbar = findViewById(R.id.student_id_toolbar)
        setSupportActionBar(profileDetailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        capturePhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        updatePhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        deletePhotoButton.setOnClickListener {
            removeImage()
        }

        val savedPhoto = sharedPreferences.getString("student_id_photo", null)
        if (savedPhoto != null) {
            hasImage = true
            studentIDPhoto.setImageBitmap(BitmapUtils.decodeBase64ToBitmap(savedPhoto))
            updateUI()
        }
    }

    /**
     * Method to start the intent for capturing image
     */
    private fun dispatchTakePictureIntent() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Student_ID_Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "Student ID Picture")
        }
        currentPhotoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    /**
     * Handling result of image capture
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            currentPhotoUri?.let { uri -> startCrop(uri) }
        }
    }

    /**
     * Handling toolbar item click
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Method to start cropping the image
     */
    private fun startCrop(uri: Uri) {
        val cropImageOptions = CropImageOptions().apply {
            guidelines = CropImageView.Guidelines.ON
            cropShape = CropImageView.CropShape.RECTANGLE
            cornerShape = CropImageView.CropCornerShape.OVAL
            fixAspectRatio = true
            aspectRatioX = 16
            aspectRatioY = 9
            activityTitle = "Crop Dal Card"
        }
        val cropImageContractOptions = CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    /**
     * Handling the result of cropping
     */
    private fun handleCropResult(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            studentIDPhoto.setImageBitmap(bitmap)
            hasImage = true

            // Save cropped photo to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("student_id_photo", BitmapUtils.encodeBitmapToBase64(bitmap))
            editor.apply()

            updateUI()
            showToast("Image saved successfully")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Update UI based on image presence
     */
    private fun updateUI() {
        if (hasImage) {
            findViewById<Button>(R.id.btn_capture_photo).visibility = Button.GONE
            findViewById<Button>(R.id.btn_update_photo).visibility = Button.VISIBLE
            findViewById<Button>(R.id.btn_delete_photo).visibility = Button.VISIBLE
        } else {
            findViewById<Button>(R.id.btn_capture_photo).visibility = Button.VISIBLE
            findViewById<Button>(R.id.btn_update_photo).visibility = Button.GONE
            findViewById<Button>(R.id.btn_delete_photo).visibility = Button.GONE
        }
    }

    /**
     * Remove image from SharedPreferences and update UI
     */
    private fun removeImage() {
        sharedPreferences.edit().remove("student_id_photo").apply()
        studentIDPhoto.setImageResource(R.drawable.id_card)
        hasImage = false
        updateUI()
        showToast("Image deleted successfully")
    }

    /**
     * Display a toast message
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
