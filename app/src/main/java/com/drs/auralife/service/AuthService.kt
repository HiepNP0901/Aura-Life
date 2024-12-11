package com.drs.auralife.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.widget.Toast
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.home.HomeActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthService : Service() {
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private val auth = Firebase.auth

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val intent = msg.obj as Intent
            when (intent.action) {
                ACTION_LOGIN -> handleLogin(intent)
                GOOGLE_SIGN_IN -> handleGoogleSignIn(intent)
                FACEBOOK_SIGN_IN -> handleFacebookSignIn(intent)
                ACTION_REGISTER -> handleRegister(intent)
                ACTION_LOGOUT -> handleLogout()
            }
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        HandlerThread("AuthServiceThread", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            msg.obj = intent
            serviceHandler?.sendMessage(msg)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceLooper?.quit()
    }

    private fun handleLogin(intent: Intent?) {
        val username = intent?.getStringExtra(EXTRA_USERNAME) ?: ""
        val password = intent?.getStringExtra(EXTRA_PASSWORD) ?: ""
        val email =
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) username else ""
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val currentUser = auth.currentUser
                if (currentUser?.isEmailVerified == false) {
                    sendResult("Please verify your email")
                } else {
                    sendResult("Login Successful", HomeActivity::class.java)
                }
            }
            .addOnFailureListener { exception ->
                sendResult(exception.message ?: "Login Failed")
            }
    }

    private fun handleGoogleSignIn(intent: Intent?) {
        sendResult("Google Sign In")
    }


    private fun handleFacebookSignIn(intent: Intent?) {
        sendResult("Facebook Sign In")
    }

    private fun handleRegister(intent: Intent?) {
        val username = intent?.getStringExtra(EXTRA_USERNAME) ?: ""
        val password = intent?.getStringExtra(EXTRA_PASSWORD) ?: ""
        val email =
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) username else ""
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val currentUser = auth.currentUser
                currentUser?.sendEmailVerification()
                sendResult(
                    "Please check your email to verify your account",
                    LoginActivity::class.java
                )
                auth.signOut()
            }
            .addOnFailureListener { exception ->
                sendResult(exception.message ?: "Registration Failed")
            }
    }

    private fun handleLogout() {
        auth.signOut()
        sendResult("Logout Successful", LoginActivity::class.java)
    }

    private fun sendResult(message: String, nextActivity: Class<*>? = null) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(RESULT).apply {
            putExtra("nextActivity", nextActivity?.name)
        }
        sendBroadcast(intent)
    }

    companion object {
        const val ACTION_LOGIN = "com.drs.food.LOGIN"
        const val GOOGLE_SIGN_IN = "com.drs.food.GOOGLE_SIGN_IN"
        const val FACEBOOK_SIGN_IN = "com.drs.food.FACEBOOK_SIGN_IN"
        const val ACTION_REGISTER = "com.drs.food.REGISTER"
        const val ACTION_LOGOUT = "com.drs.food.LOGOUT"
        const val RESULT = "com.drs.food.auth.RESULT"

        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
    }
}
