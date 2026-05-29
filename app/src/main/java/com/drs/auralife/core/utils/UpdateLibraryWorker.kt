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
import com.drs.auralife.data.remote.api.FilmAPI
import com.drs.auralife.data.remote.firebase.LibraryDataSource
import com.drs.auralife.data.remote.firebase.model.library.Library
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val CHANNEL_ID = "episode_update_channel"

class UpdateLibraryWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    private val api: FilmAPI = run {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val cache = Cache(applicationContext.cacheDir, cacheSize)
        val client = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Cache-Control", "public, max-age=${86400}")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }.build()
        Retrofit.Builder()
            .baseUrl("https://phimapi.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FilmAPI::class.java)
    }

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
                        } catch (_: Exception) {
                        }
                    }
                }
            }.awaitAll()
        }

        return updates
    }

    private suspend fun getLibrary(): List<Library> = suspendCoroutine { continuation ->
        LibraryDataSource.getLibrary { library ->
            continuation.resume(library)
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
