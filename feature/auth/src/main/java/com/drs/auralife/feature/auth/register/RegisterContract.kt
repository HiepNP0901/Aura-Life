package com.drs.auralife.feature.auth.register

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data class Success(val message: String) : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

sealed interface RegisterUiEffect {
    data class ShowToast(val message: String) : RegisterUiEffect
    data object NavigateBackWithResult : RegisterUiEffect
}
