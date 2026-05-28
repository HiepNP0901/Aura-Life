package com.drs.auralife.core.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drs.auralife.R
import com.drs.auralife.data.FilmAPI
import com.drs.auralife.data.RetrofitClient
import com.drs.auralife.data.firebase.realtime.database.user.library.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Locale

const val CHANNEL_ID = "episode_update_channel"

class UpdateLibraryWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    private val api: FilmAPI = RetrofitClient.create(applicationContext).create(FilmAPI::class.java)

    override suspend fun doWork(): Result {
        checkForNewEpisode { updates ->
            if (updates.isNotEmpty()) {
                sendNotification(applicationContext.getString(R.string.new_episodes), updates)
            }
        }
        return Result.success()
    }

    private fun checkForNewEpisode(callback: (List<String>) -> Unit) {
        val updates = mutableListOf<String>()
        var totalRequests = 0
        var completedRequests = 0

        LibraryRepository.getLibrary { library ->
            library.forEach { libraryItem ->
                libraryItem.listFilm.forEach { filmItem ->
                    totalRequests++
                    runBlocking(Dispatchers.IO) {
                        try {
                            val filmDetails = api.getFilmDetails(filmItem.slug)
                            if (filmItem.episode != filmDetails.movie.episodeCurrent) {
                                val message =
                                    if (Locale.getDefault().language == "vi") {
                                        "${filmDetails.movie.name} đã có tập mới"
                                    } else {
                                        "${filmDetails.movie.originName} has a new episode"
                                    }

                                if (!updates.contains(message)) {
                                    updates.add(message)
                                    LibraryRepository.updateEpisode(
                                        libraryItem.name,
                                        filmItem.slug,
                                        filmDetails.movie.episodeCurrent.toString(),
                                    )
                                    Notification.addNotification(applicationContext, filmItem.slug, message)
                                }
                            }
                        } catch (_: Exception) {
                        }

                        completedRequests++

                        if (completedRequests == totalRequests) {
                            callback(updates)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun sendNotification(
        title: String,
        messages: List<String>,
    ) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Episode Updates",
                    NotificationManager.IMPORTANCE_HIGH,
                )
            notificationManager.createNotificationChannel(channel)
        }

        val summary = messages.joinToString(separator = "\n")

        notificationManager.notify(
            1,
            NotificationCompat
                .Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(summary))
                .setSmallIcon(R.drawable.ic_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true)
                .build(),
        )
    }

    private fun getPendingIntent(): PendingIntent {
        val packageName = applicationContext.packageName
        val launchIntent =
            applicationContext.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        return PendingIntent.getActivity(
            applicationContext,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
