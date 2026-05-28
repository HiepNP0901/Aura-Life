package com.drs.auralife.presentation.film.play

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.realtime.database.user.history.HistoryRepository
import com.drs.auralife.data.model.film.FilmDetails
import com.drs.auralife.presentation.auth.LoginActivity
import com.drs.auralife.presentation.film.SLUG
import com.drs.auralife.presentation.payment.PaymentActivity
import com.drs.auralife.core.utils.HistoryUtils
import com.drs.auralife.core.utils.SystemUiController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@dagger.hilt.android.AndroidEntryPoint
class PlayFilmActivity : AppCompatActivity() {
    private val viewModel: FilmsViewModel by viewModels()
    private var recyclerView: RecyclerView? = null

    private var exoPlayer: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var nameFilm: TextView? = null

    private var btnPrevious: ImageButton? = null
    private var btnRewind: ImageButton? = null
    private var btnPlay: ImageButton? = null
    private var btnForward: ImageButton? = null
    private var btnNext: ImageButton? = null

    private var fullscreenButton: ImageButton? = null
    private var rotateButton: ImageButton? = null

    private var numberEpInLine = 3
    private var currentEpisode = 0
    private var currentPosition: Long = 0
    private var isFullscreen = false
    private var slug: String? = null
    private var film: FilmDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_film)

        slug = intent.getStringExtra(SLUG)

        exoPlayer = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.player_view)
        playerView?.apply {
            player = exoPlayer

            btnPlay = findViewById(R.id.btn_play_pause)
            btnRewind = findViewById(R.id.btn_rewind)
            btnForward = findViewById(R.id.btn_forward)
            btnPrevious = findViewById(R.id.btn_previous)
            btnNext = findViewById(R.id.btn_next)
            fullscreenButton = findViewById(R.id.btn_fullscreen)
            rotateButton = findViewById(R.id.rotate)
        }

        nameFilm = findViewById(R.id.nameFilm)
        recyclerView = findViewById(R.id.episodeRecyclerView)
        numberEpInLine = resources.displayMetrics.widthPixels / resources.displayMetrics.densityDpi
        recyclerView?.layoutManager = GridLayoutManager(this, ++numberEpInLine)

        slug?.let { slug ->
            HistoryRepository.getHistoryData { listHistory ->
                listHistory.find { it.slug == slug }?.let {
                    currentEpisode = it.episode
                    currentPosition = it.position
                }
                viewModel.fetchFilmDetailsLegacy(slug) { filmDetails: com.drs.auralife.data.model.film.FilmDetails? ->
                    filmDetails?.let {
                        film = it
                        playEpisode(currentEpisode)
                        recyclerView?.adapter = EpisodeAdapter(it.episodes[0].serverData) { ep ->
                            playEpisode(ep)
                        }
                    }
                }
            }
        }

        handler.postDelayed(checkPlaybackRunnable, 1000)

        settingExoPlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentEpisode", currentEpisode)
        outState.putBoolean("isFullscreen", isFullscreen)
        exoPlayer?.let {
            outState.putLong("exoPlayerPosition", it.currentPosition)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isFullscreen = savedInstanceState.getBoolean("isFullscreen", false)
        toggleFullscreen()
        currentEpisode = savedInstanceState.getInt("currentEpisode", 0)
        playEpisode(currentEpisode)
        currentPosition = savedInstanceState.getLong("exoPlayerPosition", 0) - 1000
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.apply {
            if (Authentication.isLoggedIn()) {
                HistoryRepository.addHistoryData(
                    slug.toString(),
                    currentEpisode,
                    currentPosition,
                )
            } else {
                HistoryUtils.addLocalHistory(
                    this@PlayFilmActivity,
                    slug.toString(),
                    currentEpisode,
                    currentPosition,
                )
            }
            pause()
            btnPlay?.isSelected = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        handler.removeCallbacks(checkPlaybackRunnable)
    }

    private fun playEpisode(episodeIndex: Int) {
        film?.let { filmDetails ->
            val episodes = filmDetails.episodes[0].serverData
            if (episodeIndex in episodes.indices) {
                exoPlayer?.setMediaItem(MediaItem.fromUri(episodes[episodeIndex].linkM3u8))

                exoPlayer?.prepare()
                exoPlayer?.seekTo(currentPosition)
                exoPlayer?.play()

                nameFilm?.text = episodes[episodeIndex].filename
                if (currentEpisode != episodeIndex) {
                    currentPosition = 0
                }
                currentEpisode = episodeIndex
                btnPlay?.isSelected = true
            }
        }
    }

    private fun settingExoPlayer() {
        exoPlayer?.apply {
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            playEpisode(currentEpisode + 1)
                        }
                    }
                },
            )

            btnPlay?.isSelected = true

            btnPlay?.setOnClickListener {
                if (isPlaying == true) {
                    pause()
                    btnPlay?.isSelected = false
                } else {
                    play()
                    btnPlay?.isSelected = true
                }
            }

            btnRewind?.setOnClickListener {
                val rewindPosition = currentPosition - 5000
                exoPlayer?.seekTo(rewindPosition.coerceAtLeast(0))
            }

            btnForward?.setOnClickListener {
                val forwardPosition = currentPosition + 15000
                seekTo(forwardPosition.coerceAtMost(duration))
            }

            btnPrevious?.setOnClickListener {
                playEpisode(currentEpisode - 1)
            }

            btnNext?.setOnClickListener {
                playEpisode(currentEpisode + 1)
            }

            fullscreenButton?.setOnClickListener {
                if (isFullscreen) {
                    isFullscreen = false
                    fullscreenButton!!.isSelected
                    recreate()
                } else {
                    isFullscreen = true
                    recreate()
                }
            }

            rotateButton?.setOnClickListener {
                requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }
    }

    private fun toggleFullscreen() {
        val otherViews = listOf(recyclerView, nameFilm)
        playerView?.apply {
            if (!isFullscreen) {
                SystemUiController.showSystemUI(window)
                otherViews.forEach { it?.visibility = View.VISIBLE }
                layoutParams.height = dpToPx(250f, resources.displayMetrics).toInt()
            } else {
                SystemUiController.autoHideSystemUI(window)
                otherViews.forEach { it?.visibility = View.GONE }
                layoutParams.height = resources.displayMetrics.heightPixels
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private val checkPlaybackRunnable = object : Runnable {
        override fun run() {
            exoPlayer?.apply {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val isPremium = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(
                    "ExpireDate",
                    "",
                )!! < dateFormat.format(Date())

                val maxDurationMs = 5 * 60 * 1000

                if (isPremium && currentPosition >= maxDurationMs) {
                    pause()
                    btnPlay?.isSelected = false
                    seekTo(maxDurationMs.toLong() - 1000)
                    showPremiumDialog()
                }
            }

            handler.postDelayed(this, 1000)
        }
    }

    private fun showPremiumDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.diglog_confirm, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnConfirm)

        title.text = getString(R.string.watched_more_than_5_minutes)
        btnCreate.text = getString(R.string.upgrade_now)

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnCreate.setOnClickListener {
            if (Authentication.isLoggedIn()) {
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        dialog.show()
    }
}

