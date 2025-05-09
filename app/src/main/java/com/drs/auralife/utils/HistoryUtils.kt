package com.drs.auralife.utils

import android.content.Context
import androidx.core.content.edit
import com.drs.auralife.data.firebase.realtime.database.user.history.History
import org.json.JSONArray
import org.json.JSONObject

object HistoryUtils {
    private const val PREF_NAME = "history"
    private const val KEY_HISTORY_LIST = "history_list"

    fun getLocalHistories(context: Context): MutableList<History> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(KEY_HISTORY_LIST, "[]")
        val jsonArray = JSONArray(jsonString)
        val histories = mutableListOf<History>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val slug = jsonObject.getString("slug")
            val episode = jsonObject.getInt("episode")
            val position = jsonObject.getLong("position")
            val date = jsonObject.getString("date")
            histories.add(History(slug, episode, position, date))
        }

        return histories
    }

    fun saveLocalHistories(
        context: Context,
        histories: MutableList<History>,
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        histories.forEach { history ->
            val jsonObject = JSONObject()
            jsonObject.put("slug", history.slug)
            jsonObject.put("episode", history.episode)
            jsonObject.put("position", history.position)
            jsonObject.put("date", history.date)
            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit { putString(KEY_HISTORY_LIST, jsonArray.toString()) }
    }

    fun addLocalHistory(
        context: Context,
        slug: String,
        episode: Int,
        position: Long,
    ) {
        val history = History(slug, episode, position, System.currentTimeMillis().toString())
        val histories = getLocalHistories(context)
        histories.removeIf { it.slug == slug }
        histories.add(history)
        saveLocalHistories(context, histories)
    }

    fun removeLocalHistory(
        context: Context,
        slug: String,
    ) {
        val histories = getLocalHistories(context)
        histories.removeIf { it.slug == slug }
        saveLocalHistories(context, histories)
    }
}
