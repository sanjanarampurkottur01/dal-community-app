package com.example.dalcommunity.activity.petitionactivity

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dalcommunity.R

class PetitionActivity : AppCompatActivity() {

    lateinit var petitionImage: ImageView
    private lateinit var petitionTitleEditText: EditText
    private lateinit var petitionDescEditText: EditText
    private lateinit var petitionImageView: ImageView
    // Define the SharedPreferences file name
    private val PREF_NAME = "user_details"

    // Define the key for saving userId
    private val KEY_USER_ID = "user_id"

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_petition)
        //TODO: Make this thing on very first activity of the app
        val userId = "5xQA5pUMURfJQR5Wd2NJ" // Your user ID

        // Save userId to SharedPreferences
        saveUserId(userId)


        petitionImage = findViewById(R.id.petitionImage)

        petitionImage.setOnClickListener {
            showOptions()
        }
        petitionTitleEditText = findViewById(R.id.petitionTitle)
        petitionDescEditText = findViewById(R.id.petitionDesc)
        petitionImageView = findViewById(R.id.petitionImage)
        val previewButton: Button = findViewById(R.id.previewBtn)
        previewButton.setOnClickListener {
            previewPetition()
        }
    }
    private fun saveUserId(userId: String) {
        // Get SharedPreferences instance
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Get SharedPreferences editor
        val editor = sharedPreferences.edit()

        // Put userId into SharedPreferences
        editor.putString(KEY_USER_ID, userId)

        // Apply changes
        editor.apply()
    }

    private fun showOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> dispatchPickImageIntent()
                // Handle Cancel option if needed
            }
        }
        builder.show()
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    fun dispatchPickImageIntent() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
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

    private fun previewPetition() {
        val petitionTitle = petitionTitleEditText.text.toString()
        val petitionDesc = petitionDescEditText.text.toString()

        val imageUri = saveImageToContentProvider()

        val intent = Intent(this, PreviewPetitionActivity::class.java)
        intent.putExtra("PETITION_TITLE", petitionTitle)
        intent.putExtra("PETITION_DESC", petitionDesc)
        intent.putExtra("PETITION_IMAGE_URI", imageUri.toString())
        startActivity(intent)
    }

    private fun saveImageToContentProvider(): Uri? {
        val bitmap = (petitionImage.drawable as? BitmapDrawable)?.bitmap ?: return null
        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "petition_image.png")
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
}
