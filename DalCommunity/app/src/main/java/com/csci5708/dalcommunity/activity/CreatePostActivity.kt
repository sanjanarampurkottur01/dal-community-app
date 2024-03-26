package com.csci5708.dalcommunity.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.dalcommunity.R

class CreatePostActivity : AppCompatActivity() {

    private lateinit var btnToChooseFromGallery: ImageView
    private lateinit var btnToClickPicture: ImageView
    private lateinit var btnToGetLocation: ImageView

    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        btnToChooseFromGallery = findViewById(R.id.image_gallery_icon)
        btnToClickPicture = findViewById(R.id.image_camera_icon)
        btnToGetLocation = findViewById(R.id.image_location_icon)

        btnToChooseFromGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        btnToClickPicture.setOnClickListener {
            val takePictureIntent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivity(takePictureIntent)
        }

        btnToGetLocation.setOnClickListener {
            val getLocationIntent = Intent(this, GetLocationActivity::class.java)
            startActivity(getLocationIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            // Load the selected image into imageView or do whatever you want with it
        }
    }
}
