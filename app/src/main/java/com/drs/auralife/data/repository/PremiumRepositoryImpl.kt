package com.drs.auralife.data.repository

import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.realtime.database.user.premium.PremiumRepository as FirebasePremiumRepository
import com.drs.auralife.data.mapper.FirebaseMapper.toDomainPremiumStatus
import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.repository.PremiumRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.resume

class PremiumRepositoryImpl : PremiumRepository {
    override suspend fun getPremiumStatus(): PremiumStatus {
        return suspendCancellableCoroutine { continuation ->
            FirebasePremiumRepository.getPremiumStatus { firebasePremium ->
                continuation.resume(firebasePremium.toDomainPremiumStatus())
            }
        }
    }

    override suspend fun setPremium(months: Int): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val userId = Authentication.getUserId()
            if (userId != null) {
                val userRef = FirebaseDatabase.getInstance().getReference("users")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val startDate = dateFormat.format(calendar.time)
                calendar.add(Calendar.MONTH, months)
                val expireDate = dateFormat.format(calendar.time)

                val premiumData = mapOf(
                    "status" to true,
                    "date" to startDate,
                    "expireDate" to expireDate,
                )

                userRef
                    .child(userId)
                    .child("premium")
                    .setValue(premiumData)
                    .addOnSuccessListener {
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        continuation.resume(false)
                    }
            } else {
                continuation.resume(false)
            }
        }
    }
}
