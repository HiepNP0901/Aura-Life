package com.drs.auralife.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.presentation.util.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.R
import com.drs.auralife.core.utils.Validator
import com.drs.auralife.databinding.ActivityRegisterBinding
import com.drs.auralife.presentation.common.LogoFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val registerViewModel: RegisterViewModel by viewModels()
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityRegisterBinding.inflate(inflater, container, false)
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
            findNavController().popBackStack()
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
                        is RegisterUiEffect.ShowToast -> { /* snackbar if needed */ }
                        is RegisterUiEffect.NavigateBackWithResult -> {
                            findNavController().popBackStack()
                        }
                    }
                }
        }
    }
}

