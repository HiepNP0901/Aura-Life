package com.drs.auralife.feature.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.drs.auralife.core.common.validation.Validator
import com.drs.auralife.core.navigation.AppNavigator
import com.drs.auralife.designsystem.LogoFragment
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import com.drs.auralife.feature.auth.R
import com.drs.auralife.feature.auth.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val appNavigator by lazy { AppNavigator(findNavController()) }

    private val registerViewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButton()
        setupEditText()
        observeState()
        observeEffect()

        val fragment = LogoFragment.setTitle(getString(R.string.register_title))
        childFragmentManager.beginTransaction().add(binding.containerFragment.id, fragment).commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupButton() {
        binding.createAccount.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            Validator(requireContext()).confirmPasswordValidator(password).invoke(confirmPassword)?.let {
                binding.confirmPassword.error = it
                binding.confirmPassword.requestFocus()
            }
            Validator(requireContext()).passwordValidator.invoke(password)?.let {
                binding.password.error = it
                binding.password.requestFocus()
            }
            Validator(requireContext()).emailValidator.invoke(username)?.let {
                binding.username.error = it
                binding.username.requestFocus()
            }
            if (binding.confirmPassword.error == null && binding.username.error == null && binding.password.error == null) {
                registerViewModel.register(username, password)
            }
        }

        binding.alreadyHaveAccountButton.setOnClickListener {
            appNavigator.navigateBack()
        }
    }

    private fun setupEditText() {
        binding.username.doAfterTextChanged {
            Validator(requireContext()).emailValidator.invoke(it?.toString() ?: "")?.let { msg ->
                binding.username.error = msg
            } ?: run { binding.username.error = null }
        }
        binding.password.doAfterTextChanged {
            Validator(requireContext()).passwordValidator.invoke(it?.toString() ?: "")?.let { msg ->
                binding.password.error = msg
            } ?: run { binding.password.error = null }
        }
        binding.confirmPassword.doAfterTextChanged {
            Validator(requireContext()).confirmPasswordValidator(binding.password.text.toString()).invoke(it?.toString() ?: "")?.let { msg ->
                binding.confirmPassword.error = msg
            } ?: run { binding.confirmPassword.error = null }
        }
    }

    private fun observeState() {
        launchAndRepeatWithViewLifecycle {
            registerViewModel.state.collect { state ->
                when (state) {
                    is RegisterUiState.Loading -> {
                        binding.createAccount.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is RegisterUiState.Success -> {
                        binding.createAccount.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }

                    is RegisterUiState.Error -> {
                        binding.createAccount.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        registerViewModel.resetState()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
            registerViewModel.effect.collect { effect ->
                when (effect) {
                    is RegisterUiEffect.ShowToast -> {
                        Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
                    }

                    is RegisterUiEffect.NavigateBackWithResult -> {
                        appNavigator.navigateBack()
                    }
                }
            }
        }
    }
}

