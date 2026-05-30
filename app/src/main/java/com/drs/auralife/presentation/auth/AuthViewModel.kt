package com.drs.auralife.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.LoginUseCase
import com.drs.auralife.domain.usecase.RegisterUseCase
import com.drs.auralife.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = authRepository.authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            val result = loginUseCase(username, password)
            _authState.value = result.fold(
                onSuccess = { AuthUiState.Success(it) },
                onFailure = { AuthUiState.Error(it.message ?: "Login failed") },
            )
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            val result = registerUseCase(username, password)
            _authState.value = result.fold(
                onSuccess = { AuthUiState.Success(it) },
                onFailure = { AuthUiState.Error(it.message ?: "Registration failed") },
            )
        }
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthUiState.Idle
    }
}
