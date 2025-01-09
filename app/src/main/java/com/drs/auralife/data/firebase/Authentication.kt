package com.drs.auralife.data.firebase

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.drs.auralife.R
import kotlinx.coroutines.tasks.await
import java.io.IOException

class Authentication {
    companion object{
        private val auth = Firebase.auth

        var isLoggedIn= MutableLiveData<Boolean>(isLoggedIn())

        fun isLoggedIn(): Boolean {
            return auth.currentUser != null
        }

        fun getEmail() = auth.currentUser?.email


        fun getUserId() = auth.currentUser?.uid


        suspend fun login(context: Context, username: String, password: String): Result<String> {
            return try {
                val result = auth.signInWithEmailAndPassword(username, password).await()
                val currentUser = result.user
                if (currentUser?.isEmailVerified == false) {
                    Result.failure(IOException(context.getString(R.string.please_verify_account)))
                } else {
                    Result.success(context.getString(R.string.login_successful))
                }
            } catch (_: Exception) {
                Result.failure(IOException(context.getString(R.string.login_failed)))
            }
        }


        suspend fun register(context: Context, username: String, password: String): Result<String> {
            return try {
                val result = auth.createUserWithEmailAndPassword(username, password).await()
                val currentUser = result.user
                currentUser?.sendEmailVerification()?.await() // Gửi email xác minh
                logout()
                Result.success(context.getString(R.string.please_verify_account))
            } catch (_: Exception) {
                Result.failure(IOException(context.getString(R.string.email_already_exist)))
            }
        }


        fun logout() {
            auth.signOut()
        }
    }
}