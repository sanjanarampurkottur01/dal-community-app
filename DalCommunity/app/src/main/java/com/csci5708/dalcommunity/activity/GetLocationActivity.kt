// GetLocationActivity.kt
package com.csci5708.dalcommunity.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.csci5708.dalcommunity.constants.AppConstants.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.example.dalcommunity.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GetLocationActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted, start accessing location
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start accessing location
                    getLocation()
                } else {
                    Toast.makeText(this,
                        "Location permission denied. Please grant permission to access your location.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude

                        val resultIntent = Intent()
                        resultIntent.putExtra("latitude", latitude)
                        resultIntent.putExtra("longitude", longitude)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }
        }
    }
}
