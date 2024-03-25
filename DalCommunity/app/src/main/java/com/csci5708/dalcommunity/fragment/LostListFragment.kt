package com.csci5708.dalcommunity.fragment

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.csci5708.dalcommunity.activity.PetitionActivity
import com.example.dalcommunity.Place
import com.example.dalcommunity.R
import com.example.dalcommunity.UserMap

class LostListFragment: Fragment() {
    private lateinit var locButton: Button;
    private lateinit var userMap: UserMap
    private lateinit var imageView: ImageView
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lost_listing, container, false)

        imageView = view.findViewById(R.id.lostImage);
        imageView.setOnClickListener {
            showOptions()
        }

        locButton = view.findViewById(R.id.locbutton)
        locButton.setOnClickListener {
             userMap = UserMap(
                "Memories from University",
                mutableListOf(
                    Place("Branner Hall","Describe me" ,37.426, -122.163),
                    Place("Gates CS building", "Describe me",37.430, -122.173)
                )
            )
//            var intent = Intent(activity,MapsActivity::class.java)
//            intent.putExtra("EXTRA_USER_MAP",userMap)
//            activity?.startActivity(intent)
//            val mapFragment = MapsFragment()
            val mapFragment = AddMarkerFragment()
            val bundle = Bundle()
            bundle.putSerializable("EXTRA_USER_MAP", userMap)
            mapFragment.arguments = bundle

            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragment_container, mapFragment)
                addToBackStack(null) // Add this line if you want to add the transaction to the back stack
                commit()

            }

        }
        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            val result = bundle.getInt("resultData")
            if(bundle.containsKey("EXTRA_USER_MAP")) {
                userMap = bundle.getSerializable("EXTRA_USER_MAP") as UserMap
                var textView:TextView =view.findViewById(R.id.textView2);
                textView.setText(userMap.toString())
            }

            // Handle the result. For example, you could update UI or initiate some action based on 'result'.
        }
        return view;
    }

    private fun showOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> dispatchPickImageIntent()
                // Optionally handle Cancel here
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        imageUri = createImageUri()
        imageUri?.let { uri ->
            takePictureLauncher.launch(uri)
        }
    }

    private fun dispatchPickImageIntent() {
        pickImageLauncher.launch("image/*")
    }

    private fun createImageUri(): Uri? {
        val contentResolver = requireActivity().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis().toString() + "_new_image.jpg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                imageUri?.let { uri ->
                    imageView.setImageURI(uri)
                }
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageView.setImageURI(uri)
            }
        }
    }
}