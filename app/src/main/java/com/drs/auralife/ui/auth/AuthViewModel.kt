package com.drs.auralife.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.data.AuthService
import kotlinx.coroutines.launch

class AuthViewModel() : ViewModel() {
    private val authService = AuthService()

    fun login(context: Context, username: String, password: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = authService.login(context, username, password)
            onResult(result)
        }
    }

    fun register(context: Context, username: String, password: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = authService.register(context, username, password)
            onResult(result)
        }
    }
}
