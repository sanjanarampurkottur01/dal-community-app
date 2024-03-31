package com.csci5708.dalcommunity.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.example.dalcommunity.UserMap
import com.example.dalcommunity.R
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap
    private lateinit var profileImage: ImageView
    private lateinit var userNameTextView:TextView
    private lateinit var emailTextView: TextView
    private lateinit var itemTitleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var itemImage: ImageView
    private lateinit var locationTitleTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.example.dalcommunity.R.layout.fragment_maps, container, false)
        arguments?.let {
            userMap = it.getSerializable("EXTRA_USER_MAP") as UserMap
        }
        profileImage = view.findViewById(R.id.profileImageView)
        userNameTextView = view.findViewById(R.id.userName)
        emailTextView = view.findViewById(R.id.email)
        itemTitleTextView = view.findViewById(R.id.itemTitle)
        dateTextView = view.findViewById(R.id.itemDate)
        descriptionTextView = view.findViewById(R.id.description)
        itemImage = view.findViewById(R.id.itemImage)
        itemImage.setOnClickListener {
            showImageDialog()
        }
        locationTitleTextView = view.findViewById(R.id.locationTitle)
        initializeView()



        val mapFragment = childFragmentManager
            .findFragmentById(com.example.dalcommunity.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view;
    }

    /**
     * Initializes the view with the user's information and map details.
     *
     * @param None
     * @return None
     */
    private fun initializeView() {
        if(userMap.profileImageUri.isNotEmpty()) {
            Picasso.get().load(userMap.profileImageUri).into(profileImage)
        }
        userNameTextView.setText(userMap.name)
        emailTextView.setText(userMap.email)
        itemTitleTextView.setText(userMap.itemName)
        val date = userMap.dateTime
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val parsedDate = LocalDateTime.parse(date, inputFormatter)
        val formattedDate = parsedDate.format(outputFormatter)
        val category = if (userMap.category.equals("lost")) "Lost" else "Found"
        dateTextView.text = category + " around "+formattedDate
        descriptionTextView.setText(userMap.description)
        Picasso.get().load(userMap.imageUri).into(itemImage)
        if(userMap.category.equals("found")) {
            locationTitleTextView.setText("Found at this place")
        }
    }

    /**
     * Initializes the view with the user's information and map details.
     *
     * @param None
     * @return None
     */
    private fun showImageDialog() {
        val imageDialog = Dialog(requireContext())
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        imageDialog.setContentView(R.layout.image_dialog_layout)

        val image = imageDialog.findViewById<ImageView>(R.id.imageViewDialog)
        Picasso.get().load(userMap.imageUri).into(image)

        // Making the dialog full-screen
        val window = imageDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Adding click listener to close the dialog
        image.setOnClickListener {
            imageDialog.dismiss()
        }

        imageDialog.show()
    }

    /**
     * Initializes the view with the user's information and map details.
     *
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val boundBuilder = LatLngBounds.builder()

        for(place in userMap.places) {
            val latLng = LatLng(place.latitude, place.longitude)
            boundBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet(place.description))
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),1000,1000,300))
        mMap.setOnCameraIdleListener(object : GoogleMap.OnCameraIdleListener {
            override fun onCameraIdle() {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))

                mMap.setOnCameraIdleListener(null)
            }
        })
    }

}