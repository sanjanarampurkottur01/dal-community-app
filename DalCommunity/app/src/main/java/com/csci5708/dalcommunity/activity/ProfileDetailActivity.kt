package com.csci5708.dalcommunity.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Spinner
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
import com.example.dalcommunity.R

class ProfileDetailActivity : AppCompatActivity() {
    private lateinit var profileImageView: ImageView
    private lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>

    private val interestsArray = arrayOf("Sports", "Hiking", "Programming", "Arts")
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

        val firstSpinner: Spinner = findViewById(R.id.profile_detail_first_interest_spinner)
        val secondSpinner: Spinner = findViewById(R.id.profile_detail_second_interest_spinner)
        val thirdSpinner: Spinner = findViewById(R.id.profile_detail_third_interest_spinner)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, interestsArray.toList())
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        firstSpinner.adapter = arrayAdapter
        secondSpinner.adapter = arrayAdapter
        thirdSpinner.adapter = arrayAdapter

        profileImageView = findViewById(R.id.profile_detail_image)
        imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                profileImageView.setImageURI(uri)
            }
        }

        val profileImageEditButton: ImageView = findViewById(R.id.profile_detail_image_edit_button)
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    private fun applyDim(parent: ViewGroup, dimAmount: Float) {
        val dim: Drawable = ColorDrawable(Color.BLACK)
        dim.setBounds(0, 0, parent.width, parent.height)
        dim.alpha = (255 * dimAmount).toInt()
        val overlay: ViewGroupOverlay = parent.overlay
        overlay.add(dim)
    }

    private fun clearDim(parent: ViewGroup) {
        val overlay = parent.overlay
        overlay.clear()
    }

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
            profileImageView.setImageBitmap(profileImage)
        }
    }
}