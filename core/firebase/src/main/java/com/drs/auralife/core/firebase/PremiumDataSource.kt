package com.drs.auralife.core.firebase

import android.annotation.SuppressLint
import com.drs.auralife.core.firebase.model.premium.Premium
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class PremiumDataSource @Inject constructor(
    database: FirebaseDatabase,
) {
    private val userRef = database.getReference("users")

    @SuppressLint("ConstantLocale")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getPremiumStatus(callback: (Premium) -> Unit) {
        val userId = Authentication.getUserId()
        userId?.let {
            userRef
                .child(it)
                .child("premium")
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.getValue(Premium::class.java)?.let { premium ->
                        val currentDate = dateFormat.format(Date())

                        if (premium.expireDate < currentDate) {
                            // Premium package has expired, updated status
                            val updatedPremium = premium.copy(status = false)
                            userRef
                                .child(it)
                                .child("premium")
                                .setValue(updatedPremium)
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

    fun uploadPremium(
        months: Int = 1,
        callback: (Result<Boolean>) -> Unit,
    ) {
        val userId = Authentication.getUserId()
        userId?.let { it ->
            userRef
                .child(it)
                .child("premium")
                .get()
                .addOnSuccessListener { snapshot ->
                    val currentDate = Date()
                    val calendar = Calendar.getInstance()

                    snapshot.getValue(Premium::class.java)?.let {
                        val expireDate = dateFormat.parse(it.expireDate)
                        calendar.time = if (expireDate != null && expireDate.after(currentDate)) {
                            expireDate
                        } else {
                            currentDate
                        }
                    }

                    // plus the number of months bought
                    calendar.add(Calendar.MONTH, months)
                    val newExpireDate = dateFormat.format(calendar.time)

                    val newPremium = Premium(
                        status = true,
                        date = dateFormat.format(currentDate),
                        expireDate = newExpireDate
                    )

                    userRef
                        .child(it)
                        .child("premium")
                        .setValue(newPremium)
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
