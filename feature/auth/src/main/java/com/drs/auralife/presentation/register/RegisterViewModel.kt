package com.drs.auralife.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.RegisterUseCase
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterUiEffect>()
    val effect: SharedFlow<RegisterUiEffect> = _effect.asSharedFlow()

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _state.value = RegisterUiState.Loading
            val result = registerUseCase(username, password)
            result.fold(
                onSuccess = {
                    _state.value = RegisterUiState.Success(it)
                    _effect.emit(RegisterUiEffect.NavigateBackWithResult)
                },
                onFailure = {
                    _state.value = RegisterUiState.Error(it.message ?: "Registration failed")
                    _effect.emit(RegisterUiEffect.ShowToast(it.message ?: "Registration failed"))
                },
            )
        }
    }

    fun resetState() {
        _state.value = RegisterUiState.Idle
    }
}
