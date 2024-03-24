package com.csci5708.dalcommunity.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.dalcommunity.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ScannerFragment : Fragment() {

    private lateinit var scannerTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        scannerTextView = view.findViewById(R.id.scannerTextview)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionCamera()
    }

    private fun checkPermissionCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            showAlertDialog()
        } else {
            makePermissionRequest()
        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan the QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(requireContext(), "Scanning cancelled", Toast.LENGTH_SHORT).show()
        } else {
            handleScanResult(result.contents)
        }
    }

    private fun handleScanResult(contents: String) {
        if (contents.startsWith("http://") || contents.startsWith("https://")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contents))
            startActivity(intent)
        } else {
            scannerTextView.text = contents
        }
    }

    private fun showAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Camera Permission Required")
        dialogBuilder.setMessage("This app requires access to your camera to function properly. Would you like to grant camera permission ?")
        dialogBuilder.setPositiveButton("Grant") { _, _ ->
            makePermissionRequest()
        }

        dialogBuilder.setNegativeButton("Deny") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun makePermissionRequest(){
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            showCamera()
        } else {
            Toast.makeText(requireContext(), "The feature is unavailable without the camera permission", Toast.LENGTH_SHORT).show()
        }
    }

}