package com.drs.auralife.ui.auth

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.drs.auralife.R
import com.drs.auralife.databinding.ActivityRegisterBinding
import com.drs.auralife.service.AuthService
import com.drs.auralife.ui.auth.fragment.LogoFragment

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var filter: IntentFilter
    private lateinit var receiver: BroadcastReceiver
    private lateinit var fragment: LogoFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view using the binding object
        setContentView(binding.root)

        // Initialize the fragment
        fragment = LogoFragment.setTitle(getString(R.string.register_title))
        supportFragmentManager.beginTransaction().add(binding.containerFragment.id, fragment)
            .commit()

        // Initialize the views
        username = binding.username
        password = binding.password
        confirmPassword = binding.confirmPassword
        setBindingButton()
        setBindingEditText()

        // Initialize the receiver and filter
        filter = IntentFilter(AuthService.RESULT)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                binding.createAccount.isEnabled = true
                binding.progressBar.visibility = View.GONE
                intent?.getStringExtra("nextActivity")?.let {
                    if (it.isNotEmpty()) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("RESULT", username.text.toString())
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }
    }

    // Register the receiver and filter
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, filter)
    }

    // Unregister the receiver
    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    // Set the click listeners for the buttons
    private fun setBindingButton() {
        binding.createAccount.setOnClickListener {
            if (username.text.toString().isEmpty()) {
                username.error = "This field cannot be empty"
                username.requestFocus()
            } else if (password.text.toString().isEmpty()) {
                password.error = "This field cannot be empty"
                password.requestFocus()
            } else if (confirmPassword.text.toString().isEmpty()) {
                confirmPassword.error = "This field cannot be empty"
                confirmPassword.requestFocus()
            } else if (confirmPassword.error == null && username.error == null && password.error == null) {
                binding.createAccount.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                startService(Intent(this, AuthService::class.java).apply {
                    action = AuthService.ACTION_REGISTER
                    putExtra(AuthService.EXTRA_USERNAME, username.text.toString())
                    putExtra(AuthService.EXTRA_PASSWORD, password.text.toString())
                })
            }
        }

        binding.googleButton.setOnClickListener {
            startService(Intent(this, AuthService::class.java).apply {
                action = AuthService.GOOGLE_SIGN_IN
            })
        }

        binding.facebookButton.setOnClickListener {
            startService(Intent(this, AuthService::class.java).apply {
                action = AuthService.FACEBOOK_SIGN_IN
            })
        }

        binding.alreadyHaveAccountButton.setOnClickListener {
            finish()
        }
    }

    // Set action do after text changed for the edit text
    private fun setBindingEditText() {
        binding.username.doAfterTextChanged {
            if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()) {
                username.error = "Please enter a valid email or phone number"
                username.requestFocus()
            } else {
                username.error = null
                username.requestFocus()
            }
        }

        binding.password.doAfterTextChanged {
            if (password.text.toString().length < 6) {
                password.error = "Password must be at least 6 characters"
                password.requestFocus()
            } else {
                password.error = null
                password.requestFocus()
            }
        }

        binding.confirmPassword.doAfterTextChanged {
            if (password.text.toString() != confirmPassword.text.toString()) {
                confirmPassword.error = "Passwords do not match"
                confirmPassword.requestFocus()
            } else {
                confirmPassword.error = null
            }
        }
    }
}