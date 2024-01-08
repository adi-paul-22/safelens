package com.example.safelens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : ComponentActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val textViewSignIn: TextView = findViewById(R.id.textViewSignIn)
        textViewSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val backButtonOnSignUp: ImageView = findViewById(R.id.imageBackButtonPage1)
        backButtonOnSignUp.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val registerOnApp = findViewById<Button>(R.id.buttonRegister)
        registerOnApp.setOnClickListener{
            val intent = Intent(this,GetOTPActivity::class.java)
            startActivity(intent)
        }
    }

}

