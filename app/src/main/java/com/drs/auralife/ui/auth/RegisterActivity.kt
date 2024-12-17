package com.drs.auralife.ui.auth

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.drs.auralife.R
import com.drs.auralife.databinding.ActivityRegisterBinding
import com.drs.auralife.service.AuthService
import com.drs.auralife.ui.auth.fragment.LogoFragment
import com.drs.auralife.utils.Validator

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var fragment: LogoFragment

    private val filter = IntentFilter(AuthService.RESULT)
    private val receiver = object : BroadcastReceiver() {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view using the binding object
        setContentView(binding.root)

        // Initialize the fragment
        fragment = LogoFragment.setTitle(getString(R.string.register_title))
        supportFragmentManager.beginTransaction()
            .add(binding.containerFragment.id, fragment)
            .commit()

        // Initialize the views
        username = binding.username
        password = binding.password
        confirmPassword = binding.confirmPassword
        setBindingButton()
        setBindingEditText()
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
            Validator(this).confirmPasswordValidator(password.text.toString())
                .invoke(confirmPassword.text.toString())?.let {
                    confirmPassword.error = it
                    confirmPassword.requestFocus()
                }

            Validator(this).passwordValidator.invoke(password.text.toString())?.let {
                password.error = it
                password.requestFocus()
            }

            Validator(this).emailValidator.invoke(username.text.toString())?.let {
                username.error = it
                username.requestFocus()
            }

            if (confirmPassword.error == null && username.error == null && password.error == null) {
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
            Validator(this).emailValidator.invoke(username.text.toString())?.let {
                username.error = it
                username.requestFocus()
            } ?: run {
                username.error = null
            }
        }

        binding.password.doAfterTextChanged {
            Validator(this).passwordValidator.invoke(password.text.toString())?.let {
                password.error = it
                password.requestFocus()
            } ?: run {
                password.error = null
            }
        }

        binding.confirmPassword.doAfterTextChanged {
            Validator(this).confirmPasswordValidator(password.text.toString())
                .invoke(confirmPassword.text.toString())?.let {
                    confirmPassword.error = it
                    confirmPassword.requestFocus()
                } ?: run {
                confirmPassword.error = null
            }
        }
    }
}