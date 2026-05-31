package com.drs.auralife.core.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.util.Log
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drs.auralife.R
import com.drs.auralife.data.remote.api.FilmAPI
import com.drs.auralife.data.remote.firebase.LibraryDataSource
import com.drs.auralife.core.util.Notification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.Locale

const val CHANNEL_ID = "episode_update_channel"

@HiltWorker
class UpdateLibraryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: FilmAPI,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val updates = checkForNewEpisode()
        if (updates.isNotEmpty()) {
            sendNotification(applicationContext.getString(R.string.new_episodes), updates)
        }
        return Result.success()
    }

    private suspend fun checkForNewEpisode(): List<String> {
        val library = getLibrary()
        val updates = mutableListOf<String>()

        coroutineScope {
            library.flatMap { libraryItem ->
                libraryItem.listFilm.map { filmItem ->
                    async(Dispatchers.IO) {
                        try {
                            val filmDetails = api.getFilmDetails(filmItem.slug)
                            if (filmItem.episode != filmDetails.movie.episodeCurrent) {
                                val message = if (Locale.getDefault().language == "vi") {
                                    "${filmDetails.movie.name} \u0111ã có t\u1eadp m\u1edbi"
                                } else {
                                    "${filmDetails.movie.originName} has a new episode"
                                }

                                synchronized(updates) {
                                    if (!updates.contains(message)) {
                                        updates.add(message)
                                    }
                                }

                                LibraryDataSource.updateEpisode(
                                    libraryItem.name,
                                    filmItem.slug,
                                    filmDetails.movie.episodeCurrent.toString(),
                                )
                                Notification.addNotification(applicationContext, filmItem.slug, message)
                            }
                        } catch (e: Exception) {
                            Log.e("UpdateLibraryWorker", "checkForNewEpisode failed", e)
                        }
                    }
                }
            }.awaitAll()
        }

        return updates
    }

    private suspend fun getLibrary() = LibraryDataSource.getLibrary()

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
