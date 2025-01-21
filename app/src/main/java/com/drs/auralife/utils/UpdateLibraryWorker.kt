package com.drs.auralife.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drs.auralife.R
import com.drs.auralife.data.FilmRepository
import com.drs.auralife.data.firebase.library.LibraryRepository
import java.util.Locale

const val channelId = "episode_update_channel"

class UpdateLibraryWorker(
    private val context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val repository = FilmRepository(context)

    override suspend fun doWork(): Result {
        checkForNewEpisode { updates ->
            if (updates.isNotEmpty()) {
                sendNotification(context.getString(R.string.new_episodes), updates)
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
                    repository.getFilmDetails(filmItem.slug) { it ->
                        it?.let { film ->
                            if (filmItem.episode != film.movie.episodeTotal) {
                                val message = if (Locale.getDefault().language == "vi") {
                                    "${film.movie.name} đã có tập ${film.movie.episodeTotal}"
                                } else {
                                    "${film.movie.originName} has episode ${film.movie.episodeTotal}"
                                }

                                if (!updates.contains(message)) {
                                    updates.add(message)
                                    LibraryRepository.updateEpisode(
                                        libraryItem.name, filmItem.slug, film.movie.episodeTotal
                                    )
                                    Notification.addNotification(context, filmItem.slug, message)
                                }
                            }
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
    private fun sendNotification(title: String, messages: List<String>) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Episode Updates", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val summary = messages.joinToString(separator = "\n")

        notificationManager.notify(
            1,
            NotificationCompat.Builder(applicationContext, channelId).setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(summary))
                .setSmallIcon(R.drawable.ic_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        )
    }
}
