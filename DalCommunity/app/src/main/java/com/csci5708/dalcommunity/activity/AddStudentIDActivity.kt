package com.csci5708.dalcommunity.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.csci5708.dalcommunity.constants.AppConstants
import com.example.dalcommunity.R

class AddStudentIDActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var studentIDPhoto: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student_id)

        sharedPreferences = getSharedPreferences(AppConstants.APP_SHARED_PREFERENCES, MODE_PRIVATE)
        studentIDPhoto = findViewById(R.id.student_id_photo)
        val capturePhotoButton: Button = findViewById(R.id.btn_capture_photo)

        capturePhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Load photo if already captured and stored in SharedPreferences
        val savedPhoto = sharedPreferences.getString("student_id_photo", null)
        savedPhoto?.let {
            studentIDPhoto.setImageBitmap(BitmapUtils.decodeBase64ToBitmap(it))
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            studentIDPhoto.setImageBitmap(imageBitmap)

            // Save photo to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("student_id_photo", BitmapUtils.encodeBitmapToBase64(imageBitmap))
            editor.apply()
        }
    }
}
