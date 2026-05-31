package com.drs.auralife.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.R
import com.drs.auralife.core.utils.Validator
import com.drs.auralife.databinding.ActivityLoginBinding
import com.drs.auralife.presentation.common.LogoFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityLoginBinding.inflate(inflater, container, false)
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
            findNavController().navigate(R.id.register)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }

    private fun observeEffect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.effect.collect { effect ->
                    when (effect) {
                        is LoginUiEffect.ShowToast -> { /* snackbar if needed */ }
                        is LoginUiEffect.NavigateBackWithResult -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }
}
