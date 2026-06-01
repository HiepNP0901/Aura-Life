package com.drs.auralife.data.repository

import android.content.Context
import com.drs.auralife.data.remote.firebase.Authentication
import com.drs.auralife.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AuthRepository {

    private val _authState = MutableStateFlow(Authentication.isLoggedIn())
    override val authState: StateFlow<Boolean> = _authState.asStateFlow()

    init {
        Firebase.auth.addAuthStateListener {
            val loggedIn = it.currentUser != null
            _authState.value = loggedIn
            Authentication.isLoggedIn.postValue(loggedIn)
        }
    }

    override suspend fun login(username: String, password: String): Result<String> {
        return Authentication.login(context, username, password)
    }

    override suspend fun register(username: String, password: String): Result<String> {
        return Authentication.register(context, username, password)
    }

    override fun isLoggedIn(): Boolean = Authentication.isLoggedIn()

    override fun getEmail(): String? = Authentication.getEmail()

    override fun getUserId(): String? = Authentication.getUserId()

    override fun logout() = Authentication.logout()
}
