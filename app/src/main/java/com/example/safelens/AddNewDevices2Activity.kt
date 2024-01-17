package com.example.safelens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.safelens.databinding.ActivityAddNewDevice2Binding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest
import java.util.jar.Manifest

class AddNewDevice2Activity : ComponentActivity(){
    private lateinit var binding: ActivityAddNewDevice2Binding
    private lateinit var dbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewDevice2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageBackButton.setOnClickListener{
            val intent = Intent(this,AllDevicesActivity::class.java)
            startActivity(intent)
        }
        binding.buttonGenerateID.setOnClickListener {
            saveCameraToDatabase()
        }
    }

    private fun saveCameraToDatabase() {
        val model = binding.enterModelName.text.toString().trim()
        val resolution = binding.enterResolution.text.toString().trim()
        val dpi = binding.enterDPI.text.toString().trim()
        val serial_no = binding.enterSerialNo.text.toString().trim()
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
            val newCamera = camera(aadhar,address1, address2, cameraID,country,  dpi, district, model, pincode, resolution, serial_no, state,"camera")

            databaseReference.child(pushKey).setValue(newCamera)
                .addOnSuccessListener {
                    // Log success message
                    Log.d("AddNewDeviceActivity", "Camera saved successfully with ID: $it")
                    navigateToAllDevicesActivity()
                }}
            .addOnFailureListener { exception ->
                // Log error message
                Log.e("AddNewDeviceActivity", "Error saving camera: ${exception.message}")

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


}