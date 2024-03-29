package com.example.safelens
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.safelens.databinding.ActivityGetOtpBinding
import com.example.safelens.databinding.ActivitySignInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class GetOTPActivity :ComponentActivity(){
    private lateinit var binding: ActivityGetOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        phoneNumber = intent.getStringExtra("phone")!!

        binding = ActivityGetOtpBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.buttonGetOTP.setOnClickListener {
            phoneNumber = "+91$phoneNumber"
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this,"Authenticated Successfully",Toast.LENGTH_SHORT).show()
                    sendToLogin()
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                        Log.d("TAG","signInWithPhoneAUthCredential: ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    private fun sendToLogin() {
        startActivity(Intent(this,LoginActivity::class.java))
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG","onVerificationFailed : $e")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG","onVerificationFailed : $e")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            val intent = Intent(this@GetOTPActivity,EnterOTPActivity::class.java)
            intent.putExtra("OTP",verificationId)
            intent.putExtra("resendToken",token)
            intent.putExtra("phoneNumber",phoneNumber)
            startActivity(intent)

        }


    }

}