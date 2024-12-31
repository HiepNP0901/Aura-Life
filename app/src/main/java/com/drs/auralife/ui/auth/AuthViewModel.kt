package com.drs.auralife.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.data.AuthService
import kotlinx.coroutines.launch

class AuthViewModel() : ViewModel() {

    fun login(context: Context, username: String, password: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = AuthService.login(context, username, password)
            onResult(result)
        }
    }

    fun register(context: Context, username: String, password: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = AuthService.register(context, username, password)
            onResult(result)
        }
    }
}
