package com.drs.auralife.utils

import android.content.Context
import com.drs.auralife.R
import java.time.Duration
import java.time.Instant

class Time {
    companion object{
        fun calculateTimeDifference(updateTime: String, context: Context): String {
            val updateInstant = Instant.parse(updateTime) - Duration.ofHours(7)
            val currentInstant = Instant.now()

            val duration = Duration.between(updateInstant, currentInstant)
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
}