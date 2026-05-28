package com.drs.auralife.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.data.firebase.Authentication
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    fun login(
        context: Context,
        username: String,
        password: String,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            val result = Authentication.login(context, username, password)
            onResult(result)
        }
    }

    fun register(
        context: Context,
        username: String,
        password: String,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            val result = Authentication.register(context, username, password)
            onResult(result)
        }
    }
}

