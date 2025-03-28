package com.drs.auralife.data.firebase.realtime.database.user.premium

import android.annotation.SuppressLint
import com.drs.auralife.data.firebase.Authentication
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

object PremiumRepository {
    private val userRef = FirebaseDatabase.getInstance().getReference("users")

    @SuppressLint("ConstantLocale")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getPremiumStatus(callback: (Premium) -> Unit) {
        val userId = Authentication.getUserId()
        userId?.let {
            userRef.child(it).child("premium").get().addOnSuccessListener { snapshot ->
                snapshot.getValue(Premium::class.java)?.let { premium ->
                    val currentDate = dateFormat.format(Date())

                    if (premium.expireDate < currentDate) {
                        // Premium package has expired, updated status
                        val updatedPremium = premium.copy(status = false)
                        userRef.child(it).child("premium").setValue(updatedPremium)
                            .addOnSuccessListener {
                                callback(updatedPremium)
                            }
                    } else {
                        callback(premium)
                    }
                } ?: run {
                    // If there is no Premium data, the default is False
                    callback(Premium(status = false, date = "", expireDate = ""))
                }
            }.addOnFailureListener {
                // If there is an error when taking data, returning the default state
                callback(Premium(status = false, date = "", expireDate = ""))
            }
        }
    }

    fun uploadPremium(months: Int = 1, callback: (Result<Boolean>) -> Unit) {
        val userId = Authentication.getUserId()
        userId?.let {
            userRef.child(it).child("premium").get().addOnSuccessListener { snapshot ->
                val currentDate = Date()
                val calendar = Calendar.getInstance()
                calendar.time = currentDate

                val existingPremium = snapshot.getValue(Premium::class.java)
                val newStartDate = existingPremium?.let {
                    // If the user has a premium and has not expired, then from the date of the old expiration
                    val expireDate = dateFormat.parse(it.expireDate)
                    if (expireDate != null && expireDate.after(currentDate)) {
                        calendar.time = expireDate
                    }
                    expireDate
                } ?: currentDate // If there is no Premium or has expired, starting today

                // plus the number of months bought
                calendar.add(Calendar.MONTH, months)
                val newExpireDate = dateFormat.format(calendar.time)

                val newPremium = Premium(status = true, date = dateFormat.format(newStartDate), expireDate = newExpireDate)

                userRef.child(it).child("premium").setValue(newPremium)
                    .addOnSuccessListener {
                        callback(Result.success(true))
                    }.addOnFailureListener { e ->
                        callback(Result.failure(Exception(e)))
                    }
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
        }
    }
}