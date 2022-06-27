package com.ntduc.logingoogletutorial

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ntduc.logingoogletutorial.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnSignIn.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            activityResultLauncher.launch(signInIntent)
        }

        binding.btnSignOut.setOnClickListener {
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(this) {
                    updateUI(null)
                }
        }

        binding.btnDisconnect.setOnClickListener {
            mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this) {
                    updateUI(null)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("testlogin", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account == null) {
            binding.txtInfo.visibility = View.INVISIBLE
            binding.txtInfo.text = ""
            binding.btnSignIn.visibility = View.VISIBLE
            binding.btnSignOut.visibility = View.INVISIBLE
            binding.btnDisconnect.visibility = View.INVISIBLE
        } else {
            binding.txtInfo.visibility = View.VISIBLE
            binding.txtInfo.text = account.email
            binding.btnSignIn.visibility = View.INVISIBLE
            binding.btnSignOut.visibility = View.VISIBLE
            binding.btnDisconnect.visibility = View.VISIBLE
        }
    }
}