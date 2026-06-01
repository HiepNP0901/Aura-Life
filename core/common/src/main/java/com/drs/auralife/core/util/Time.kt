package com.drs.auralife.core.util

import android.content.Context
import com.drs.auralife.core.common.R
import java.time.Duration
import java.time.Instant

object Time {
    fun calculateTimeDifference(
        updateInstant: Instant,
        context: Context,
    ): String {
        val duration = Duration.between(updateInstant, Instant.now())
        val days = duration.toDays()
        val hours = duration.toHours()
        val minutes = duration.toMinutes()

        return when {
            days > 0 -> days.toString() + context.getString(R.string.days_ago)
            hours > 0 -> (hours % 24).toString() + context.getString(R.string.hours_ago)
            minutes > 0 -> (minutes % 60).toString() + context.getString(R.string.minutes_ago)
            else -> context.getString(R.string.just_finished)
        }
    }
}
