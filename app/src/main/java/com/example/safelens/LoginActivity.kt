package com.example.safelens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import androidx.activity.ComponentActivity
import com.example.safelens.databinding.ActivityLoginBinding


class LoginActivity : ComponentActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.buttonRegister.setOnClickListener{
            val nameRegister = binding.enterName.text.toString()
            val emailRegister = binding.enterEmail.text.toString()
            val aadharRegister = binding.enterAadhar.text.toString()
            val phoneNoRegister = binding.enterPhoneNo.text.toString()
            val passwordRegister = binding.enterPassword.text.toString()

            if(nameRegister.isEmpty()) Toast.makeText(this,"Name field must be filled",Toast.LENGTH_SHORT).show()
            if(emailRegister.isEmpty()) Toast.makeText(this,"Email field must be filled",Toast.LENGTH_SHORT).show()
            if(aadharRegister.isEmpty()) Toast.makeText(this,"Aadhar field must be filled",Toast.LENGTH_SHORT).show()
            if(phoneNoRegister.isEmpty()) Toast.makeText(this,"Phone number field must be filled",Toast.LENGTH_SHORT).show()
            if(passwordRegister.isEmpty()) Toast.makeText(this,"Password field must be filled",Toast.LENGTH_SHORT).show()

            binding.textSignIn.setOnClickListener{
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
            binding.imageBackButtonPage1.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            if(nameRegister.isNotEmpty() && emailRegister.isNotEmpty() && aadharRegister.isNotEmpty() && phoneNoRegister.isNotEmpty() && passwordRegister.isNotEmpty()){
                firebaseAuth.createUserWithEmailAndPassword(emailRegister,passwordRegister).addOnCompleteListener{
                task-> if(task.isSuccessful){
                    val userId = firebaseAuth.currentUser?.uid
                    val user = User(nameRegister, emailRegister, aadharRegister, phoneNoRegister)
                    userId?.let{
                        FirebaseDatabase.getInstance().getReference("https://safelens-d12fa-default-rtdb.firebaseio.com/users").child(it).setValue(user).addOnCompleteListener { databaseTask ->
                            if(databaseTask.isSuccessful){
                                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this, "Failed to register user details", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                }
            }
        }

    }

}

