package com.drs.auralife.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drs.auralife.R
import com.drs.auralife.core.utils.Validator
import com.drs.auralife.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fragment: LogoFragment
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        fragment = LogoFragment.setTitle(getString(R.string.login_title))
        supportFragmentManager
            .beginTransaction()
            .add(binding.containerFragment.id, fragment)
            .commit()

        username = binding.username
        password = binding.password
        setBindingButton()
        setBindingEditText()
        observeAuthState()

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            runOnUiThread {
                if (result.resultCode == RESULT_OK) {
                    val data = result.data?.getStringExtra(USERNAME)
                    username.setText(data)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        val orientation = when (display.rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> LinearLayout.VERTICAL
            Surface.ROTATION_90, Surface.ROTATION_270 -> LinearLayout.HORIZONTAL
            else -> LinearLayout.VERTICAL
        }
        binding.linearLayout.orientation = orientation
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    when (state) {
                        is AuthUiState.Loading -> {
                            binding.loginButton.isEnabled = false
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is AuthUiState.Success -> {
                            binding.loginButton.isEnabled = true
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is AuthUiState.Error -> {
                            binding.loginButton.isEnabled = true
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                            authViewModel.resetState()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

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
                authViewModel.login(username.text.toString(), password.text.toString())
            }
        }

        binding.dontHaveAccountButton.setOnClickListener {
            resultLauncher.launch(Intent(this, RegisterActivity::class.java))
        }
    }

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
