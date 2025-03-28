package com.drs.auralife.ui.film.play

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.realtime.database.user.history.HistoryRepository
import com.drs.auralife.data.model.film.FilmDetails
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.film.SLUG
import com.drs.auralife.utils.HistoryUtils
import com.drs.auralife.utils.SystemUiController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayFilmActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: FilmsViewModel

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var nameFilm: TextView

    private lateinit var btnPrevious: ImageButton
    private lateinit var btnRewind: ImageButton
    private lateinit var btnPlay: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var btnNext: ImageButton

    private lateinit var fullscreenButton: ImageButton
    private lateinit var rotateButton: ImageButton

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

        viewModel = ViewModelProvider(this, FilmViewModelFactory(this))[FilmsViewModel::class.java]

        exoPlayer = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.player_view)
        playerView.player = exoPlayer

        btnPlay = playerView.findViewById(R.id.btn_play_pause)
        btnRewind = playerView.findViewById(R.id.btn_rewind)
        btnForward = playerView.findViewById(R.id.btn_forward)
        btnPrevious = playerView.findViewById(R.id.btn_previous)
        btnNext = playerView.findViewById(R.id.btn_next)
        fullscreenButton = playerView.findViewById(R.id.btn_fullscreen)
        rotateButton = playerView.findViewById(R.id.rotate)

        nameFilm = findViewById(R.id.nameFilm)
        recyclerView = findViewById(R.id.episodeRecyclerView)
        numberEpInLine = resources.displayMetrics.widthPixels/resources.displayMetrics.densityDpi
        recyclerView.layoutManager = GridLayoutManager(this, ++numberEpInLine)

        slug?.let { slug ->
            HistoryRepository.getHistoryData { listHistory ->
                listHistory.find { it.slug == slug }.let {
                    it?.let {
                        currentEpisode = it.episode
                        currentPosition = it.position
                    }
                }
                viewModel.fetchFilmDetails(slug) { filmDetails ->
                    filmDetails?.let {
                        film = it
                        playEpisode(currentEpisode)
                        recyclerView.adapter = EpisodeAdapter(it.episodes[0].serverData) { ep ->
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
        outState.putLong("exoPlayerPosition", exoPlayer.currentPosition)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isFullscreen = savedInstanceState.getBoolean("isFullscreen", false)
        toggleFullscreen()
        currentEpisode = savedInstanceState.getInt("currentEpisode", 0)
        playEpisode(currentEpisode)
        currentPosition = savedInstanceState.getLong("exoPlayerPosition", 0) - 1000
    }


    override fun onPause() {
        super.onPause()
        if (Authentication.isLoggedIn()) {
            HistoryRepository.addHistoryData(
                slug.toString(), currentEpisode, exoPlayer.currentPosition
            )
        }
        else {
            HistoryUtils.addHistory(
                this, slug.toString(), currentEpisode, exoPlayer.currentPosition
            )
        }
        exoPlayer.pause()
        btnPlay.isSelected = false
    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        handler.removeCallbacks(checkPlaybackRunnable)
    }


    private fun playEpisode(episodeIndex: Int) {
        film?.let { filmDetails ->
            val episodes = filmDetails.episodes[0].serverData
            if (episodeIndex in episodes.indices) {
                exoPlayer.setMediaItem(MediaItem.fromUri(episodes[episodeIndex].linkM3u8))

                exoPlayer.prepare()
                exoPlayer.seekTo(currentPosition)
                exoPlayer.play()

                nameFilm.text = episodes[episodeIndex].filename
                if (currentEpisode != episodeIndex) {
                    currentPosition = 0
                }
                currentEpisode = episodeIndex
                btnPlay.isSelected = true
            }
        }
    }


    private fun settingExoPlayer() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    playEpisode(currentEpisode + 1)
                }
            }
        })

        btnPlay.isSelected = true

        btnPlay.setOnClickListener {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                btnPlay.isSelected = false
            }
            else {
                exoPlayer.play()
                btnPlay.isSelected = true
            }
        }

        btnRewind.setOnClickListener {
            val rewindPosition = exoPlayer.currentPosition - 5000
            exoPlayer.seekTo(rewindPosition.coerceAtLeast(0))
        }

        btnForward.setOnClickListener {
            val forwardPosition = exoPlayer.currentPosition + 15000
            exoPlayer.seekTo(forwardPosition.coerceAtMost(exoPlayer.duration))
        }

        btnPrevious.setOnClickListener {
            playEpisode(currentEpisode - 1)
        }

        btnNext.setOnClickListener {
            playEpisode(currentEpisode + 1)
        }

        fullscreenButton.setOnClickListener {
            if (isFullscreen) {
                isFullscreen = false
                fullscreenButton.isSelected
                recreate()
            }
            else {
                isFullscreen = true
                recreate()
            }
        }

        rotateButton.setOnClickListener {
            requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }


    private fun toggleFullscreen() {
        val otherViews = listOf(recyclerView, nameFilm)

        if (!isFullscreen) {
            SystemUiController.showSystemUI(window)
            otherViews.forEach { it.visibility = View.VISIBLE }
            playerView.layoutParams.height = dpToPx(250f, resources.displayMetrics).toInt()
        }
        else {
            SystemUiController.autoHideSystemUI(window)
            otherViews.forEach { it.visibility = View.GONE }
            playerView.layoutParams.height = resources.displayMetrics.heightPixels
        }
    }


    private val handler = Handler(Looper.getMainLooper())

    private val checkPlaybackRunnable = object : Runnable {
        override fun run() {
            @SuppressLint("ConstantLocale")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val isPremium = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(
                "ExpireDate", ""
            )!! < dateFormat.format(Date())

            val position = exoPlayer.currentPosition
            val maxDurationMs = 5 * 60 * 1000

            if (isPremium && position >= maxDurationMs) {
                exoPlayer.pause()
                btnPlay.isSelected = false
                exoPlayer.seekTo(maxDurationMs.toLong() - 1000)
                showPremiumDialog()
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
            //aaaaa
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        dialog.show()
    }
}