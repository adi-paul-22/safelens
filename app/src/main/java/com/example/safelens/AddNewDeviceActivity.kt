package com.example.safelens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.example.safelens.databinding.ActivityAddNewDeviceBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class AddNewDeviceActivity : ComponentActivity() {
    private lateinit var binding: ActivityAddNewDeviceBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.imageBackButton.setOnClickListener {
            val intent = Intent(this, AllDevicesActivity::class.java)
            startActivity(intent)
        }

        binding.buttonGenerateID.setOnClickListener {
            if (checkLocationPermission()) {
                getLocationAndSaveCamera()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getLocationAndSaveCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude


                saveCameraToDatabase()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocationAndSaveCamera()
                } else {
                    Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveCameraToDatabase() {
        val model = binding.enterModelName.text.toString().trim()
        val resolution = binding.enterResolution.text.toString().trim()
        val dpi = binding.enterDPI.text.toString().trim()
        val address1 = binding.enterAddress1.text.toString().trim()
        val address2 = binding.enterAddress2.text.toString().trim()
        val district = binding.enterDistrict.text.toString().trim()
        val pincode = binding.enterPinCode.text.toString().trim()
        val state = binding.enterState.text.toString().trim()
        val country = binding.enterCountry.text.toString().trim()
        val lastFourDigits = if (pincode.length >= 4) pincode.takeLast(4) else pincode
        val databaseReference = FirebaseDatabase.getInstance().getReference("cameras")
        val pushKey = databaseReference.push().key ?: ""

        val hashedKey = hashKey(pushKey).take(6)  // Taking only first 6 characters of the hash
        val cameraID = "RJ${lastFourDigits}${hashedKey}"
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId ?: "")
        userRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            val aadhar = user?.aadhar ?: ""
            val lat = latitude.toString()
            val longi = longitude.toString()
            val newCamera = cameraWithoutDVR(aadhar, address1, address2, cameraID, country, dpi, district, lat, longi, model, pincode, resolution, "null", state,"camera")

            databaseReference.child(pushKey).setValue(newCamera)
                .addOnSuccessListener {
                    Log.d("AddNewDeviceActivity", "Camera saved successfully with ID: $cameraID")
                    navigateToAllDevicesActivity()
                }
                .addOnFailureListener { exception ->
                    Log.e("AddNewDeviceActivity", "Error saving camera: ${exception.message}")
                }
        }
    }

    private fun navigateToAllDevicesActivity() {
        val intent = Intent(this, AllDevicesActivity::class.java)
        startActivity(intent)
    }

    private fun hashKey(key: String): String {
        val bytes = key.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
