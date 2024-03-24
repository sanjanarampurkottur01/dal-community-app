package com.csci5708.dalcommunity.fragment

import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dalcommunity.Place
import com.example.dalcommunity.R
import com.example.dalcommunity.UserMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class AddMarkerFragment: Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap
    private var markers: MutableList<Marker> = mutableListOf()
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(com.example.dalcommunity.R.layout.fragment_add_marker, container, false)
        arguments?.let {
            userMap = it.getSerializable("EXTRA_USER_MAP") as UserMap
        }
        val saveButton: Button = view.findViewById(R.id.save)
         searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Perform final search when search button is pressed
                // For example, querying a database or filtering a data list
                val location = searchView.query.toString()
                if(location.isNotEmpty()) {
                    val geocoder = Geocoder(requireContext())
                    val addressList = geocoder.getFromLocationName(location,1)?: listOf()
                    if(addressList.isNotEmpty() && addressList.size>0) {
                        val address = addressList.get(0)!!
                        val latLng = LatLng(address.latitude,address.longitude)
                        showAlertDialog(latLng)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))

                    }

                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Perform live search as the user types
                // For example, filtering a RecyclerView adapter as the query text changes

                return false
            }
        })
        saveButton.setOnClickListener {
            if (isAdded && activity != null) {
                // Remove the fragment from its parent activity
                val result = Bundle().apply {
                    putInt("resultData", userMap.places.size)
                    putSerializable("EXTRA_USER_MAP",userMap)
                }
                //activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                parentFragmentManager.setFragmentResult("result", result)
                parentFragmentManager.popBackStack()
                //activity?.supportFragmentManager?.popBackStack()
            }
        }

        val mapFragment = childFragmentManager
            .findFragmentById(com.example.dalcommunity.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.view?.let {
            Snackbar.make(it, "Long press to add a marker!", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", {})
                .setActionTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                .show()
        }
        return view;
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val boundBuilder = LatLngBounds.builder()

        for (place in userMap.places) {
            val latLng = LatLng(place.latitude, place.longitude)
            boundBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet(place.description))
        }

        mMap.setOnInfoWindowClickListener { markerToDelete ->
            markers.remove(markerToDelete)
            val place  = userMap.places.filter {
                x->x.latitude ==markerToDelete.position.latitude && x.longitude == markerToDelete.position.longitude
            }
            if(place.size>0) {
                userMap.places.remove(place.get(0))
            }
            markerToDelete.remove()
        }

        mMap.setOnMapLongClickListener { latLng ->
            showAlertDialog(latLng)

        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 1000, 1000, 0))
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(context).inflate(R.layout.dialog_create_place, null)
        val dialog =
            AlertDialog.Builder(requireContext())
                .setTitle("Create a marker")
                .setView(placeFormView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null)
                .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description =
                placeFormView.findViewById<EditText>(R.id.etDescription).text.toString()
            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(
                    context,
                    "Place must have non-empty title and description",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val marker = mMap.addMarker(
                MarkerOptions().position(latLng).title(title).snippet(description).draggable(true)
            )
            markers.add(marker!!)
            userMap.places.add(Place(title,description,latLng.latitude,latLng.longitude))
            dialog.dismiss()
        }

    }
}