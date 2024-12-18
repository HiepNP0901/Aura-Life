package com.drs.auralife.data

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.IOException
import com.drs.auralife.R

class AuthResponsible {
    private val auth = Firebase.auth

    fun login(context: Context,username: String, password: String, callback: (Result<String>) -> Unit){
        auth.signInWithEmailAndPassword(username, password)
            .addOnSuccessListener {
                val currentUser = auth.currentUser
                if (currentUser?.isEmailVerified == false) {
                    callback(Result.failure(IOException(context.getString(R.string.please_verify_account))))
                } else{
                    callback(Result.success(context.getString(R.string.login_successful)))
                }
            }
            .addOnFailureListener {
                callback(Result.failure(IOException(context.getString(R.string.login_failed))))
            }
    }

    fun register(context: Context, username: String, password: String, callback: (Result<String>) -> Unit){
        auth.createUserWithEmailAndPassword(username, password)
            .addOnSuccessListener {
                val currentUser = auth.currentUser
                currentUser?.sendEmailVerification()
                callback(Result.success(context.getString(R.string.please_verify_account)))
            }
            .addOnFailureListener {
                callback(Result.failure(IOException(context.getString(R.string.email_already_exist))))
            }
    }

    fun logout(){
        auth.signOut()
    }
}