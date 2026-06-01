package com.drs.auralife.core.util

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object AppNotification {
    private const val PREF_NAME = "notifications"
    private const val KEY_NOTIFICATION_LIST = "notification_list"

    fun getNotifications(context: Context): MutableList<Pair<String, String>> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(KEY_NOTIFICATION_LIST, "[]")
        val jsonArray = JSONArray(jsonString)
        val notifications = mutableListOf<Pair<String, String>>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val slug = jsonObject.getString("slug")
            val content = jsonObject.getString("content")
            notifications.add(Pair(slug, content))
        }
        return notifications
    }

    fun saveNotifications(
        context: Context,
        notifications: MutableList<Pair<String, String>>,
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        notifications.forEach { (slug, content) ->
            val jsonObject = JSONObject()
            jsonObject.put("slug", slug)
            jsonObject.put("content", content)
            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit().putString(KEY_NOTIFICATION_LIST, jsonArray.toString()).apply()
    }

    fun addNotification(
        context: Context,
        slug: String,
        content: String,
    ) {
        val notifications = getNotifications(context)
        notifications.add(Pair(slug, content))
        saveNotifications(context, notifications)
    }

    fun removeNotification(
        context: Context,
        notification: Pair<String, String>,
    ) {
        val notifications = getNotifications(context)
        notifications.remove(notification)
        saveNotifications(context, notifications)
    }

    fun removeAllNotification(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_NOTIFICATION_LIST).apply()
    }
}
