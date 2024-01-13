package com.example.safelens

import android.content.Intent
import com.example.safelens.databinding.ActivityAllDevicesBinding
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.safelens.databinding.ActivityGetOtpBinding
import com.example.safelens.databinding.ActivityNotificationsBinding
import com.example.safelens.databinding.ActivityVerifyOtpBinding
import com.google.firebase.auth.FirebaseAuth

class NotificationsActivity : ComponentActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icon2.setOnClickListener{
            val intent = Intent(this,AllDevicesActivity::class.java)
            startActivity(intent)
        }

        binding.icon3.setOnClickListener{
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}