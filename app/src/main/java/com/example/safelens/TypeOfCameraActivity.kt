package com.example.safelens

import android.content.Intent
import com.example.safelens.databinding.ActivityAllDevicesBinding
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.safelens.databinding.ActivityGetOtpBinding
import com.example.safelens.databinding.ActivityNotificationsBinding
import com.example.safelens.databinding.ActivityTypeOfCameraBinding
import com.example.safelens.databinding.ActivityVerifyOtpBinding
import com.google.firebase.auth.FirebaseAuth

class TypeOfCameraActivity : ComponentActivity() {
    private lateinit var binding: ActivityTypeOfCameraBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeOfCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button1.setOnClickListener{
            val intent = Intent(this,AddNewDeviceActivity::class.java)
            startActivity(intent)
        }
        binding.button2.setOnClickListener{
            val intent = Intent(this,AddNewDevice2Activity::class.java)
            startActivity(intent)
        }
    }
}