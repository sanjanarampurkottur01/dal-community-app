// CreatePostActivity.kt
package com.csci5708.dalcommunity.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dalcommunity.R

class CreatePostActivity : AppCompatActivity() {

    private lateinit var btnToChooseFromGallery: ImageView
    private lateinit var btnToClickPicture: ImageView
    private lateinit var btnToGetLocation: ImageView
    private lateinit var postCaption: EditText
    private lateinit var locationTextView: TextView

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_GET_LOCATION = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        btnToChooseFromGallery = findViewById(R.id.image_gallery_icon)
        btnToClickPicture = findViewById(R.id.image_camera_icon)
        btnToGetLocation = findViewById(R.id.image_location_icon)
        postCaption = findViewById(R.id.post_content_edit_text)

        postCaption.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        btnToChooseFromGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        btnToClickPicture.setOnClickListener {
            val takePictureIntent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivity(takePictureIntent)
        }

        btnToGetLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_GET_LOCATION
                )
            } else {
                // Permission has already been granted, start accessing location
                startGetLocationActivity()
            }
        }
    }

    private fun startGetLocationActivity() {
        val getLocationIntent = Intent(this, GetLocationActivity::class.java)
        startActivityForResult(getLocationIntent, REQUEST_GET_LOCATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            // Load the selected image into imageView or do whatever you want with it
        } else if (requestCode == REQUEST_GET_LOCATION && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)

            Log.e("RETVAL", latitude.toString())
            Log.e("RETVAL", latitude.toString())
        }
    }
}
