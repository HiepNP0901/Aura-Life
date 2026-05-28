package com.drs.auralife.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.data.firebase.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
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

