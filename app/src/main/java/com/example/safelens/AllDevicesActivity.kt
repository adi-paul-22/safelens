package com.example.safelens

import android.app.AlertDialog
import android.content.Intent
import com.example.safelens.databinding.ActivityAllDevicesBinding
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safelens.databinding.ActivityGetOtpBinding
import com.example.safelens.databinding.ActivityVerifyOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllDevicesActivity : ComponentActivity() {
    private lateinit var binding: ActivityAllDevicesBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var useRecyclerView: RecyclerView
    private lateinit var cameraArrayList: ArrayList<camera>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        useRecyclerView = findViewById(R.id.userList)
        useRecyclerView.layoutManager = LinearLayoutManager(this)
        useRecyclerView.setHasFixedSize(true)

        cameraArrayList = arrayListOf<camera>()
        getCameraData()

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddNewDeviceActivity::class.java)
            startActivity(intent)
        }
        binding.icon1.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }
        binding.icon3.setOnClickListener {
            val intent1 = Intent(this, ProfileActivity::class.java)
            startActivity(intent1)
        }
    }

    private fun getCameraData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId ?: "")

        userRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            val userAadhar = user?.aadhar


            dbref = FirebaseDatabase.getInstance().getReference("cameras")
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        cameraArrayList.clear()
                        for (camSnap in snapshot.children) {
                            val newCam = camSnap.getValue(camera::class.java)
                            if (newCam?.aadhar == userAadhar) {
                                cameraArrayList.add(newCam!!)
                            }
                        }
                        if (cameraArrayList.isEmpty()) {
                            binding.tvNoDevices.visibility = View.VISIBLE
                            useRecyclerView.visibility = View.GONE
                        } else {
                            binding.tvNoDevices.visibility = View.GONE
                            useRecyclerView.visibility = View.VISIBLE
                        }
                        useRecyclerView.adapter = MyAdapter(cameraArrayList) { camera, action ->
                            when (action) {
                                "details" -> showCameraDetailsPopup(camera)
                                "delete" -> deleteCameraFromFirebase(camera)
                            }
                        }

                    }else {
                        binding.tvNoDevices.visibility = View.VISIBLE
                        useRecyclerView.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }.addOnFailureListener {

        }
    }

    private fun showCameraDetailsPopup(camera: camera) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Camera Details")

        val message = """
        Camera ID: ${camera.cameraID}
        Aadhar: ${camera.aadhar}
        Address 1: ${camera.address1}
        Address 2: ${camera.address2}
        District: ${camera.district}
        State: ${camera.state}
        Country: ${camera.country}
        Model: ${camera.model}
        Resolution: ${camera.resolution}
        DPI: ${camera.dpi}
        Serial No: ${camera.serial_no}
    """.trimIndent()

        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteCameraFromFirebase(camera: camera) {
        // Confirmation dialog
        AlertDialog.Builder(this)
            .setTitle("Delete Camera")
            .setMessage("Are you sure you want to delete this camera?")
            .setPositiveButton("Yes") { dialog, _ ->
                // Perform the deletion
                val dbRef = FirebaseDatabase.getInstance().getReference("cameras")
                dbRef.child(camera.cameraID).removeValue().addOnSuccessListener {
                    Toast.makeText(this, "Camera deleted successfully", Toast.LENGTH_SHORT).show()
                    cameraArrayList.remove(camera)
                    useRecyclerView.adapter?.notifyDataSetChanged() // Refresh the list
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to delete camera", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}