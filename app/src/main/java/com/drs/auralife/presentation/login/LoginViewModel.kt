package com.drs.auralife.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginUiEffect>()
    val effect: SharedFlow<LoginUiEffect> = _effect.asSharedFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginUiState.Loading
            val result = loginUseCase(username, password)
            result.fold(
                onSuccess = {
                    _state.value = LoginUiState.Success(it)
                    _effect.emit(LoginUiEffect.NavigateBackWithResult)
                },
                onFailure = {
                    _state.value = LoginUiState.Error(it.message ?: "Login failed")
                    _effect.emit(LoginUiEffect.ShowToast(it.message ?: "Login failed"))
                },
            )
        }
    }

    fun resetState() {
        _state.value = LoginUiState.Idle
    }
}
