package com.drs.auralife.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.drs.auralife.R
import com.drs.auralife.databinding.ActivityLoginBinding
import com.drs.auralife.ui.MainActivity
import com.drs.auralife.utils.Validator

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fragment: LogoFragment
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view using the binding object
        setContentView(binding.root)

        // Initialize the fragment
        fragment = LogoFragment.setTitle(getString(R.string.login_title))
        supportFragmentManager.beginTransaction()
            .add(binding.containerFragment.id, fragment)
            .commit()

        // Initialize the views
        username = binding.username
        password = binding.password
        setBindingButton()
        setBindingEditText()

        // Initialize the result launcher
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            runOnUiThread {
                if (result.resultCode == RESULT_OK) {
                    val data = result.data?.getStringExtra("RESULT")
                    username.setText(data)
                }
            }
        }
    }

    // Register the receiver and filter
    override fun onStart() {
        super.onStart()
        @Suppress("DEPRECATION") val display = windowManager.defaultDisplay
        val orientation = when (display.rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> LinearLayout.VERTICAL
            Surface.ROTATION_90, Surface.ROTATION_270 -> LinearLayout.HORIZONTAL
            else -> LinearLayout.VERTICAL // Default to vertical
        }
        binding.linearLayout.orientation = orientation
    }

    // Set the click listeners for the buttons
    private fun setBindingButton() {
        binding.loginButton.setOnClickListener {
            Validator(this).passwordValidator.invoke(password.text.toString())?.let {
                password.error = it
                password.requestFocus()
            }

            Validator(this).emailValidator.invoke(username.text.toString())?.let {
                username.error = it
                username.requestFocus()
            }

            if (username.error == null && password.error == null) {
                binding.loginButton.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                AuthViewModel().login(this, username.text.toString(), password.text.toString()) { result ->
                    if (result.isSuccess) {
                        Toast.makeText(this, result.getOrNull(), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else {
                        binding.loginButton.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, result.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.dontHaveAccountButton.setOnClickListener {
            resultLauncher.launch(Intent(this, RegisterActivity::class.java))
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
    }
}