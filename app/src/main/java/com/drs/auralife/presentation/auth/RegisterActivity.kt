package com.drs.auralife.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drs.auralife.R
import com.drs.auralife.core.utils.Validator
import com.drs.auralife.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val USERNAME = "@username"

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var fragment: LogoFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        fragment = LogoFragment.setTitle(getString(R.string.register_title))
        supportFragmentManager
            .beginTransaction()
            .add(binding.containerFragment.id, fragment)
            .commit()

        username = binding.username
        password = binding.password
        confirmPassword = binding.confirmPassword
        setBindingButton()
        setBindingEditText()
        observeAuthState()
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                when (state) {
                    is AuthUiState.Loading -> {
                        binding.createAccount.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AuthUiState.Success -> {
                        binding.createAccount.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                        val resultIntent = Intent()
                        resultIntent.putExtra(USERNAME, username.text.toString())
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                    is AuthUiState.Error -> {
                        binding.createAccount.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }
                    else -> {}
                }
                }
            }
        }
    }

    private fun setBindingButton() {
        binding.createAccount.setOnClickListener {
            Validator(this)
                .confirmPasswordValidator(password.text.toString())
                .invoke(confirmPassword.text.toString())
                ?.let {
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
                authViewModel.register(username.text.toString(), password.text.toString())
            }
        }

        binding.alreadyHaveAccountButton.setOnClickListener {
            finish()
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

        binding.confirmPassword.doAfterTextChanged {
            Validator(this)
                .confirmPasswordValidator(password.text.toString())
                .invoke(confirmPassword.text.toString())
                ?.let {
                    confirmPassword.error = it
                    confirmPassword.requestFocus()
                } ?: run {
                confirmPassword.error = null
            }
        }
    }
}
