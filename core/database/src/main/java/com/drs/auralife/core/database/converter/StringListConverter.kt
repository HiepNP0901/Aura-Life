package com.drs.auralife.core.database.converter

import androidx.room.TypeConverter
import org.json.JSONArray

class StringListConverter {

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { JSONArray(it).toString() }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val arr = JSONArray(it)
            (0 until arr.length()).map { arr.getString(it) }
        }
    }
}
