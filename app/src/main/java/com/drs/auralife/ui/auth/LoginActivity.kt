package com.drs.auralife.ui.auth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.drs.auralife.R
import com.drs.auralife.databinding.ActivityLoginBinding
import com.drs.auralife.service.AuthService
import com.drs.auralife.ui.auth.fragment.LogoFragment
import com.drs.auralife.utils.Validator

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fragment: LogoFragment
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val filter= IntentFilter(AuthService.RESULT)
    private val receiver= object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding.loginButton.isEnabled = true
            binding.progressBar.visibility = View.GONE
            intent?.getStringExtra("nextActivity")?.let {
                startActivity(Intent(this@LoginActivity, Class.forName(it)))
                finishAffinity()
            }
        }
    }

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
        registerReceiver(receiver, filter)
        @Suppress("DEPRECATION") val display = windowManager.defaultDisplay
        val orientation = when (display.rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> LinearLayout.VERTICAL
            Surface.ROTATION_90, Surface.ROTATION_270 -> LinearLayout.HORIZONTAL
            else -> LinearLayout.VERTICAL // Default to vertical
        }
        binding.mainLayout.orientation = orientation
    }

    // Unregister the receiver
    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
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
                startService(Intent(this, AuthService::class.java).apply {
                    action = AuthService.ACTION_LOGIN
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