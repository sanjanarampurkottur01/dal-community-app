package com.csci5708.dalcommunity.fragment

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipDescription
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.example.dalcommunity.Place
import com.example.dalcommunity.R
import com.example.dalcommunity.UserMap
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar

class LostFoundItemFragment: Fragment() {
    private lateinit var dateEditText: EditText;
    private lateinit var descriptionEditText: EditText
    private lateinit var itemNameEditText: EditText
    private var date =0
    private var month=0;
    private var year=0;
    private var hour=0;
    private var minute=0;
    private lateinit var imageView: ImageView
    private lateinit var lostFoundRadioGroup: RadioGroup
    private lateinit var radioButtonLost: RadioButton
    private lateinit var radioButtonFound: RadioButton
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var email =""
    private var name =""
    private lateinit var userMap: UserMap
    private lateinit var addMarkersButton:AppCompatButton
    private lateinit var saveButton:AppCompatButton
    private lateinit var deleteButton: AppCompatButton
    private var imageSet=false
    private lateinit var storageRef: StorageReference

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 101
        const val REQUEST_STORAGE_PERMISSION = 102
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lost_found_item, container, false)
        storageRef = FirebaseStorage.getInstance().reference.child("lostandfound")
        dateEditText = view.findViewById(R.id.dateEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        lostFoundRadioGroup = view.findViewById(R.id.lostFoundRadioGroup)
        radioButtonFound = view.findViewById(R.id.radioButtonFound)
        radioButtonLost = view.findViewById(R.id.radioButtonLost)
        itemNameEditText = view.findViewById(R.id.itemNameEditText)
        imageView = view.findViewById(R.id.lostImage);
        addMarkersButton = view.findViewById(R.id.addMarkersButton)
        saveButton = view.findViewById(R.id.saveButton)
        deleteButton = view.findViewById(R.id.deleteButton)
        deleteButton.visibility = View.GONE
        saveButton.setOnClickListener {
            saveListing()
        }
        addMarkersButton.setOnClickListener {
            addMarker()
        }
        deleteButton.setOnClickListener {
            deleteItem()
        }
        userMap = UserMap()
        userMap.email = Firebase.auth.currentUser?.email.toString()
        FireStoreSingleton.getData(
            "users",
            Firebase.auth.currentUser?.email.toString(),
            { d: DocumentSnapshot -> getUserDataOnSuccess(d) },
            { e -> getUserDataOnFailure() }
        )
        imageView.setOnClickListener {
            val checkPermission = checkPermission()
            if(checkPermission()) {
                showOptions()

            }
            else {
                requestPermissions()
            }
        }
        dateEditText.setOnClickListener {
            setDateTime()

        }
        arguments?.let {
            if(it.containsKey("EXTRA_USER_ITEM")) {
                populateViews(it.getSerializable("EXTRA_USER_ITEM") as UserMap)
            }
        }
        parentFragmentManager.setFragmentResultListener("addMarker", this) { _, bundle ->
            val result = bundle.getInt("resultData")
            if(bundle.containsKey("EXTRA_USER_MAP")) {
                userMap = bundle.getSerializable("EXTRA_USER_MAP") as UserMap
            }

        }
        return view;
    }

    /**
     * Deletes an item from the lost and found collection in Firebase Firestore.
     *
     * This function is called when an item needs to be deleted from the collection. It first checks if the deletion was successful by passing a callback function to the `deleteData` method of `FireStoreSingleton`. If the deletion is successful, a success toast message is displayed. If the deletion fails, an error toast message is displayed.
     *
     * After the deletion, the function checks if the fragment is still added to its parent activity. If it is, it removes the fragment from its parent activity by setting a fragment result with the key "itemModified" and a boolean value of true. It then pops the back stack to navigate back to the previous fragment.
     *
     */
    private fun deleteItem() {
        val onComplete = { b: Boolean ->
            if (b) {
                Toast.makeText(
                    requireContext(),
                    "Item Deleted Successfully!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to Delete Item!",
                    Toast.LENGTH_LONG
                ).show()
            }

            if (isAdded && activity != null) {
                val result = Bundle().apply {
                    putBoolean("itemModified", true)
                }
                parentFragmentManager.setFragmentResult("itemModified", result)
                parentFragmentManager.popBackStack()
            }
        }
        FireStoreSingleton.deleteData("lostAndFound",userMap.id,onComplete)
    }

    
    /**
     * Populates the views with the data from the given UserMap object.
     *
     * @param existingUserMap the UserMap object containing the data to populate the views
     */
    private fun populateViews(existingUserMap: UserMap) {
        userMap = existingUserMap
        itemNameEditText.setText(userMap.itemName)
        descriptionEditText.setText(userMap.description)
        dateEditText.setText(userMap.dateTime)
        when (userMap.category) {
            "lost" -> radioButtonLost.isChecked = true
            "found" -> radioButtonFound.isChecked = true
        }
        if(imageUri == null) {
            imageSet = true
            Picasso.get().load(userMap.imageUri).into(imageView)
        }
        addMarkersButton.setText("Edit Markers")
        deleteButton.visibility = View.VISIBLE
    }


    override fun onResume() {
        super.onResume()
        // Check if imageUri is not null and imageView is initialized
        if (imageUri != null) {
            imageView.setImageURI(imageUri)
            imageSet = true
        }
    }

    /**
     * Saves the listing information entered by the user.
     *
     * @return void
     */
    private fun saveListing() {
        var itemName = itemNameEditText.text.toString()
        var itemDescription = descriptionEditText.text.toString()
        var dateTime = dateEditText.text.toString()
        if(itemName.isNullOrEmpty()) {
            Toast.makeText(requireContext(),"Please enter name of item",Toast.LENGTH_SHORT).show()
            return
        }
        if(itemDescription.isNullOrEmpty()) {
            Toast.makeText(requireContext(),"Please enter description of item",Toast.LENGTH_SHORT).show()
            return
        }
        if(dateTime.isNullOrEmpty()) {
            Toast.makeText(requireContext(),"Please enter datetime for lost or found item",Toast.LENGTH_SHORT).show()
            return
        }
        if(!imageSet) {
            Toast.makeText(requireContext(),"Please upload image for lost or found item",Toast.LENGTH_SHORT).show()
            return
        }
        val checkedRadioButtonId = lostFoundRadioGroup.checkedRadioButtonId
        if(checkedRadioButtonId==-1) {
            Toast.makeText(requireContext(),"Please check atleast one option for lost and found type",Toast.LENGTH_SHORT).show()
            return
        }
        if(userMap.places.size==0) {
            Toast.makeText(requireContext(),"Please set alleast one marker for lost or found item",Toast.LENGTH_SHORT).show()
            return
        }
        userMap.itemName = itemName
        userMap.description = itemDescription
        userMap.dateTime = dateTime
        userMap.category = when (checkedRadioButtonId) {
            R.id.radioButtonLost -> "lost"
            R.id.radioButtonFound -> "found"
            else -> ""
        }
        setUserData()

    }

    /**
     * Adds a marker to the map.
     */
    private fun addMarker() {
        val mapFragment = AddMarkerFragment()
        val bundle = Bundle()
        bundle.putSerializable("EXTRA_USER_MAP", userMap)
        mapFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.home_fragment_container, mapFragment)
            addToBackStack(null) // Add this line if you want to add the transaction to the back stack
            commit()

        }
    }

    /**
     * Sets the date and time values for the current date and time.
     *
     * This function retrieves the current date and time using the `Calendar` class and sets the values
     * for the `date`, `month`, `year`, `hour`, and `minute` variables. It also initializes a `Calendar`
     * object and sets up a `TimePickerDialog.OnTimeSetListener` to handle the selection of a time. The
     * selected time is then set in the `myCalendar` object. Finally, a `DatePickerDialog.OnDateSetListener`
     * is set up to handle the selection of a date. The selected date is set in the `myCalendar` object and
     * a `TimePickerDialog` is shown to allow the user to select a time.
     *
     * @return void
     */
    private fun setDateTime() {
        val calender = Calendar.getInstance()
        date = calender.get(Calendar.DATE)
        month =calender.get(Calendar.MONTH)
        year = calender.get(Calendar.YEAR)
        hour = calender.get(Calendar.HOUR)
        minute = calender.get(Calendar.MINUTE)
        val myCalendar = Calendar.getInstance()
        val timeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR,hourOfDay)
            myCalendar.set(Calendar.MINUTE,minute)
            val dateFormat ="yyyy-MM-dd HH:mm:ss"
            val simpleDateFormat = SimpleDateFormat(dateFormat)

            dateEditText.setText(simpleDateFormat.format(myCalendar.time))
        }
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            val timePickerDialog = TimePickerDialog(requireContext(),timeListener,hour,minute,true)
            timePickerDialog.show()

        }
        val datePickerDialog = DatePickerDialog(requireContext(),listener,year,month,date)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()-1000
        datePickerDialog.show()


    }

    /**
     * Shows a dialog with options to take a photo, choose from the gallery, or cancel.
     *
     */
    private fun showOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> dispatchPickImageIntent()
            }
        }
        builder.show()
    }

    /**
     * Dispatches an intent to take a picture and saves it to the device.
     *
     * @return nothing
     */
    private fun dispatchTakePictureIntent() {
        imageUri = createImageUri()
        imageUri?.let { uri ->
            takePictureLauncher.launch(uri)
        }
    }

    /**
     * Dispatches an intent to pick an image from the device's media library.
     *
     * This function launches an activity that allows the user to select an image from their device's
     * media library. The selected image is returned as a result, and can be accessed through the
     * [pickImageLauncher] object.
     *
     */
    private fun dispatchPickImageIntent() {
        pickImageLauncher.launch("image/*")
    }

    /**
     * Creates a new image URI using the content resolver.
     *
     * @return The URI of the newly created image, or null if an error occurred.
     */
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
                    imageSet = true
                }
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = uri
                imageView.setImageURI(uri)
                imageSet = true
            }
        }
    }

    /**
     * Checks if the camera permission is granted.
     *
     * @return true if the camera permission is granted, false otherwise
     */
    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            false
        } else true
    }

    /**
     * Requests the necessary permissions for the camera and external storage.
     *
     * This function uses the `ActivityCompat.requestPermissions` method to request the
     * `CAMERA` and `WRITE_EXTERNAL_STORAGE` permissions. The permissions are requested
     * through the `requestPermissions` method of the `requireActivity()` object.
     *
     * @param None
     * @return None
     */  
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            LostFoundItemFragment.REQUEST_CAMERA_PERMISSION
        )
    }

    /**
     * Overrides the onRequestPermissionsResult function.
     *
     * @param  requestCode   the request code
     * @param  permissions    the array of permissions
     * @param  grantResults   the array of grant results
     * @return                void
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LostFoundItemFragment.REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, do your task
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getUserDataOnFailure() {
        Toast.makeText(activity, "Failed to get user data", Toast.LENGTH_LONG).show()
    }

    /**
     * Retrieves user data from the given document snapshot and updates the userMap object.
     *
     * @param doc The document snapshot containing the user data.
     */
    private fun getUserDataOnSuccess(doc: DocumentSnapshot) {
        val userDetails = doc.data
        userMap.name = userDetails?.get("name").toString()
        userMap.profileImageUri = userDetails?.get("photoUri")?.toString() ?: ""
    }

    /**
     * Sets the user data based on the current state of the user map and image URI.
     *
     * @throws NullPointerException if the user map or image URI is null.
     */
    private fun setUserData() {
        if (userMap.id.isEmpty()) {
            storageRef = storageRef.child(userMap.email + System.currentTimeMillis().toString())
            imageUri?.let {
                storageRef.putFile(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            userMap.imageUri = uri.toString()
                            val onComplete = { b: Boolean ->
                                if (b) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Item Posted Successfully!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to save Item!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                if (isAdded && activity != null) {
                                    // Remove the fragment from its parent activity
                                    val result = Bundle().apply {
                                        putBoolean("itemModified", true)
                                    }
                                    //activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                                    parentFragmentManager.setFragmentResult("itemModified", result)
                                    parentFragmentManager.popBackStack()
                                    //activity?.supportFragmentManager?.popBackStack()
                                }
                            }
                            FireStoreSingleton.addData("lostAndFound", userMap, onComplete)

                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        else if(imageUri!=null) {
            storageRef = storageRef.child(userMap.email + System.currentTimeMillis().toString())
            imageUri?.let {
                storageRef.putFile(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            userMap.imageUri = uri.toString()
                            val onComplete = { b: Boolean ->
                                if (b) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Item Edited Successfully!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to save Item!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                if (isAdded && activity != null) {
                                    // Remove the fragment from its parent activity
                                    val result = Bundle().apply {
                                        putBoolean("itemModified", true)
                                    }
                                    //activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                                    parentFragmentManager.setFragmentResult("itemModified", result)
                                    parentFragmentManager.popBackStack()
                                    //activity?.supportFragmentManager?.popBackStack()
                                }
                            }
                            FireStoreSingleton.addData("lostAndFound",userMap.id,userMap, onComplete)

                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        else {
            val onComplete = { b: Boolean ->
                if (b) {
                    Toast.makeText(
                        requireContext(),
                        "Item Edited Successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to save Item!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                if (isAdded && activity != null) {
                    // Remove the fragment from its parent activity
                    val result = Bundle().apply {
                        putBoolean("itemModified", true)
                    }
                    //activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                    parentFragmentManager.setFragmentResult("itemModified", result)
                    parentFragmentManager.popBackStack()
                    //activity?.supportFragmentManager?.popBackStack()
                }
            }
            FireStoreSingleton.addData("lostAndFound",userMap.id,userMap, onComplete)
        }
    }
}