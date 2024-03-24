package com.csci5708.dalcommunity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.example.dalcommunity.UserMap
import com.example.dalcommunity.R

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.example.dalcommunity.R.layout.fragment_maps, container, false)
        arguments?.let {
            userMap = it.getSerializable("EXTRA_USER_MAP") as UserMap
        }
        val saveButton:Button = view.findViewById(R.id.save)
        saveButton.setOnClickListener {
            if (isAdded && activity != null) {
                // Remove the fragment from its parent activity
                val result = Bundle().apply {
                    putInt("resultData", 2)
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
        return view;
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val boundBuilder = LatLngBounds.builder()

        for(place in userMap.places) {
            val latLng = LatLng(place.latitude, place.longitude)
            boundBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet(place.description))
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),1000,1000,0))
    }

}