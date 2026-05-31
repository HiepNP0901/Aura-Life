package com.drs.auralife.data.remote.firebase

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.drs.auralife.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.io.IOException

object Authentication {
    private val auth = Firebase.auth
    var isLoggedIn = MutableLiveData(isLoggedIn())

    fun isLoggedIn() = auth.currentUser != null

    fun getEmail() = auth.currentUser?.email

    fun getUserId() = auth.currentUser?.uid

    suspend fun login(
        context: Context,
        username: String,
        password: String,
    ): Result<String> =
        try {
            val result = auth.signInWithEmailAndPassword(username, password).await()
            val currentUser = result.user
            if (currentUser?.isEmailVerified == false) {
                Result.failure(IOException(context.getString(R.string.please_verify_account)))
            } else {
                Result.success(context.getString(R.string.login_successful))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun register(
        context: Context,
        username: String,
        password: String,
    ): Result<String> =
        try {
            val result = auth.createUserWithEmailAndPassword(username, password).await()
            val currentUser = result.user
            currentUser?.sendEmailVerification()?.await() // Gửi email xác minh
            logout()
            Result.success(context.getString(R.string.please_verify_account))
        } catch (e: Exception) {
            Log.e("Authentication", "register failed", e)
            Result.failure(IOException(e.message ?: context.getString(R.string.email_already_exist)))
        }

    fun logout() {
        auth.signOut()
    }
}
