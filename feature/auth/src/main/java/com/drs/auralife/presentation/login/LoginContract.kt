package com.drs.auralife.presentation.login

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val message: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

sealed interface LoginUiEffect {
    data class ShowToast(val message: String) : LoginUiEffect
    data object NavigateBackWithResult : LoginUiEffect
}
