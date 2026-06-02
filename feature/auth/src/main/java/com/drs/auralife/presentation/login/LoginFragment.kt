package com.drs.auralife.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.feature.auth.R
import com.drs.auralife.navigation.NavRoutes
import com.drs.auralife.core.common.validation.Validator
import com.drs.auralife.feature.auth.databinding.FragmentLoginBinding
import com.drs.auralife.designsystem.LogoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButton()
        setupEditText()
        observeState()
        observeEffect()

        val fragment = LogoFragment.setTitle(getString(R.string.login_title))
        childFragmentManager.beginTransaction().add(binding.containerFragment.id, fragment).commit()
    }

    override fun onStart() {
        super.onStart()
        @Suppress("DEPRECATION")
        val display = requireActivity().windowManager.defaultDisplay
        val orientation = when (display.rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> LinearLayout.VERTICAL
            Surface.ROTATION_90, Surface.ROTATION_270 -> LinearLayout.HORIZONTAL
            else -> LinearLayout.VERTICAL
        }
        binding.linearLayout.orientation = orientation
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupButton() {
        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            Validator(requireContext()).passwordValidator.invoke(password)?.let {
                binding.password.error = it
                binding.password.requestFocus()
            }
            Validator(requireContext()).emailValidator.invoke(username)?.let {
                binding.username.error = it
                binding.username.requestFocus()
            }
            if (binding.username.error == null && binding.password.error == null) {
                loginViewModel.login(username, password)
            }
        }

        binding.dontHaveAccountButton.setOnClickListener {
            findNavController().navigate(NavRoutes.REGISTER)
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
    }

    private fun observeState() {
        launchAndRepeatWithViewLifecycle {
                loginViewModel.state.collect { state ->
                    when (state) {
                        is LoginUiState.Loading -> {
                            binding.loginButton.isEnabled = false
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is LoginUiState.Success -> {
                            binding.loginButton.isEnabled = true
                            binding.progressBar.visibility = View.GONE
                        }
                        is LoginUiState.Error -> {
                            binding.loginButton.isEnabled = true
                            binding.progressBar.visibility = View.GONE
                            loginViewModel.resetState()
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                loginViewModel.effect.collect { effect ->
                    when (effect) {
                        is LoginUiEffect.ShowToast -> {
                            Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is LoginUiEffect.NavigateBackWithResult -> {
                            findNavController().popBackStack()
                        }
                    }
                }
        }
    }
}

