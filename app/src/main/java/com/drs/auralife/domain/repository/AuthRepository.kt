package com.drs.auralife.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<String>
    suspend fun register(username: String, password: String): Result<String>
    fun isLoggedIn(): Boolean
    fun getEmail(): String?
    fun getUserId(): String?
    fun logout()
    val authState: StateFlow<Boolean>
}
