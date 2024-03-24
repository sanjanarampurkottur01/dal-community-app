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

/**
 * Activity for creating a new post.
 */
class CreatePostActivity : AppCompatActivity() {

    private lateinit var btnToChooseFromGallery: ImageView
    private lateinit var btnToClickPicture: ImageView
    private lateinit var btnToGetLocation: ImageView
    private lateinit var postCaption: EditText
    private lateinit var locationTextView: TextView
    private lateinit var btnToCreatePollPost: ImageView
    private lateinit var gridView: GridView

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_GET_LOCATION = 2

    /**
     * Called when the activity is starting.
     * Initializes UI elements and sets up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        btnToChooseFromGallery = findViewById(R.id.image_gallery_icon)
        btnToClickPicture = findViewById(R.id.image_camera_icon)
        btnToCreatePollPost = findViewById(R.id.image_poll_icon)
       // gridView = findViewById(R.id.gridView)
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

        btnToCreatePollPost.setOnClickListener {
            val createPollPostIntent = Intent(this, CreatePollPostActivity::class.java)
            startActivity(createPollPostIntent)
        }
    }

    /**
     * Starts the GetLocationActivity to obtain user's location.
     */
    private fun startGetLocationActivity() {
        val getLocationIntent = Intent(this, GetLocationActivity::class.java)
        startActivityForResult(getLocationIntent, REQUEST_GET_LOCATION)
    }

    /**
     * Callback method to be invoked when an activity that launched exits, giving the requestCode it started with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you
     * to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent
     * "extras").
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            // Load the selected image into imageView or do whatever you want with it
        } else if (requestCode == REQUEST_GET_LOCATION && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)

            Log.e("RETVAL", latitude.toString())
            Log.e("RETVAL", longitude.toString())
        }
    }
}
