package com.example.safelens

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.safelens.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : ComponentActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load user information
        loadUserProfile()

        binding.icon2.setOnClickListener {
            val intent = Intent(this, AllDevicesActivity::class.java)
            startActivity(intent)
        }

        binding.icon1.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        // Sign out button listener
        binding.signOutButton.setOnClickListener {
            showSignOutConfirmationDialog()
        }
    }

    private fun loadUserProfile() {
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val userProfile = dataSnapshot.getValue(User::class.java)
                    binding.userName.text = userProfile?.name
                    binding.userEmail.text = userProfile?.email
                    binding.userPhone.text = userProfile?.phone
                    binding.userAadhar.text = userProfile?.aadhar
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showSignOutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you want to sign out?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // Sign out from Firebase and navigate to LoginActivity
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog and stay on the profile page
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Sign Out")
        alert.show()
    }

}
