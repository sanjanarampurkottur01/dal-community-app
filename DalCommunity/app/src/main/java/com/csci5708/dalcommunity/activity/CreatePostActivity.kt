// CreatePostActivity.kt
package com.csci5708.dalcommunity.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.Post
import com.csci5708.dalcommunity.model.TextPost
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Activity for creating a new post.
 */
class CreatePostActivity : AppCompatActivity() {

    private lateinit var btnToChooseFromGallery: ImageView
    private lateinit var btnToClickPicture: ImageView
    private lateinit var btnToGetLocation: ImageView
    private lateinit var btnToCreatePollPost: ImageView
    private lateinit var postCaption: EditText
    private lateinit var postBtn: Button
    private lateinit var locationTextView: TextView
    lateinit var imageView: ImageView

    private val REQUEST_GALLERY = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_GET_LOCATION = 3

    var postType: Int = 0
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

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
        btnToGetLocation = findViewById(R.id.image_location_icon)
        btnToCreatePollPost = findViewById(R.id.image_poll_icon)
        postCaption = findViewById(R.id.post_content_edit_text)
        imageView = findViewById(R.id.image_preview)
        postBtn = findViewById(R.id.post_button)

        postCaption.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        btnToChooseFromGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        btnToClickPicture.setOnClickListener {
            val takePictureIntent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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


        postBtn.setOnClickListener {
            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val currentDate = Date()
            val postId = FirebaseFirestore.getInstance().collection("post").document().id
            if (postType == 0) {
                val postToPush = TextPost(postId,
                    Firebase.auth.currentUser?.email.toString(),
                    0,
                    Firebase.auth.currentUser?.displayName.toString(),
                    dateFormat.format(currentDate),
                    postCaption.text.toString(),
                    listOf(""),
                    latitude,
                    longitude
                )
                postToFirebase(postToPush)
                finish()
            } else if (postType == 1) {
                val postToPush = ImagePost(postId,
                    Firebase.auth.currentUser?.email.toString(),
                    1,
                    Firebase.auth.currentUser?.displayName.toString(),
                    dateFormat.format(currentDate),
                    "gs://dal-community-01.appspot.com/post-images/$postId.jpg",
                    postCaption.text.toString(),
                    listOf(""),
                    latitude,
                    longitude
                )
                postToFirebase(postToPush)
                uploadImageToFirebase(postId)
            }
        }
    }

    private fun postToFirebase(postToPush: Post) {
        FireStoreSingleton.addData("post", postToPush, {
            Toast.makeText(this, "Post Successful", Toast.LENGTH_SHORT).show()
        }, {
            Toast.makeText(this, "Post Failed", Toast.LENGTH_SHORT).show()
        })
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
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Handle result from camera
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    postType = 1
                    imageView.setImageBitmap(imageBitmap)
                    imageView.visibility = View.VISIBLE
                    // Optionally adjust the height of the caption EditText
                    val newHeightPixels = (64 * resources.displayMetrics.density).toInt()
                    val layoutParams = postCaption.layoutParams
                    layoutParams.height = newHeightPixels
                    postCaption.layoutParams = layoutParams
                }

                REQUEST_GET_LOCATION -> {
                    // Handle result from location activity
                    latitude = data!!.getDoubleExtra("latitude", 0.0)
                    longitude = data.getDoubleExtra("longitude", 0.0)
                }

                else -> {
                    // Handle result from gallery
                    val imageUri = data?.data
                    imageView.setImageURI(imageUri)
                    if (imageUri != null) {
                        postType = 1
                        imageView.visibility = View.VISIBLE
                        // Optionally adjust the height of the caption EditText
                        val newHeightPixels = (64 * resources.displayMetrics.density).toInt()
                        val layoutParams = postCaption.layoutParams
                        layoutParams.height = newHeightPixels
                        postCaption.layoutParams = layoutParams
                    } else {
                        postType = 0
                        imageView.visibility = View.GONE
                        // Optionally adjust the height of the caption EditText
                        val newHeightPixels = (200 * resources.displayMetrics.density).toInt()
                        val layoutParams = postCaption.layoutParams
                        layoutParams.height = newHeightPixels
                        postCaption.layoutParams = layoutParams
                    }
                }
            }
        }
    }

    private fun uploadImageToFirebase(postId: String) {
        val storageRef = FirebaseStorage.getInstance().getReference()
        val folderRef = storageRef.child("/post-images/$postId.jpg")

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = folderRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Upload failure", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
