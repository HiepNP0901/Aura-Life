package com.drs.auralife.ui.film.play

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import com.drs.auralife.data.model.film.FilmDetails
import com.drs.auralife.ui.home.SLUG

class PlayFilmActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: FilmsViewModel
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var btnRewind: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var fullscreenButton: ImageButton
    private lateinit var nameFilm: TextView
    private var currentEpisode = 0
    private var currentPosition: Long = 0
    private var slug: String? = null
    private var film: FilmDetails? = null
    private var isFullscreen = false
    private var numberEpInLine = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_film)

        slug = intent.getStringExtra(SLUG)

        viewModel = ViewModelProvider(this, FilmViewModelFactory(this))[FilmsViewModel::class.java]

        exoPlayer = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.player_view)
        playerView.player = exoPlayer

        btnRewind = playerView.findViewById(R.id.btn_rewind)
        btnForward = playerView.findViewById(R.id.btn_forward)
        btnPrevious = playerView.findViewById(R.id.btn_previous)
        btnNext = playerView.findViewById(R.id.btn_next)
        fullscreenButton = playerView.findViewById(R.id.btn_fullscreen)

        nameFilm = findViewById(R.id.nameFilm)
        recyclerView = findViewById(R.id.episodeRecyclerView)
        numberEpInLine = resources.displayMetrics.widthPixels/resources.displayMetrics.densityDpi
        recyclerView.layoutManager = GridLayoutManager(this, ++numberEpInLine)
    }


    override fun onStart() {
        super.onStart()
        setFilmDetail { filmDetails ->
            filmDetails?.let {
                film = it
                playEpisode(currentEpisode)
                recyclerView.adapter = EpisodeAdapter(it.episodes[0].serverData) { ep ->
                    playEpisode(ep)
                }
            }
        }

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


    private fun setFilmDetail(callback: (FilmDetails?) -> Unit) {
        slug?.let {
            viewModel.fetchFilmDetails(it) { filmDetails ->
                callback(filmDetails)
            }
        }
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
                currentEpisode = episodeIndex
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
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                fullscreenButton.isSelected
                recreate()
            }
            else {
                isFullscreen = true
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                recreate()
            }
        }
    }


    private fun toggleFullscreen(){
        val otherViews = listOf(recyclerView, nameFilm)

        if (!isFullscreen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowInsetsController = window.insetsController
                windowInsetsController?.show(WindowInsets.Type.systemBars())
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }

            otherViews.forEach { it.visibility = View.VISIBLE }
            playerView.layoutParams.height = 700
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowInsetsController = window.insetsController
                windowInsetsController?.let {
                    it.hide(WindowInsets.Type.systemBars())
                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        )
            }

            otherViews.forEach { it.visibility = View.GONE }
            playerView.layoutParams.height = resources.displayMetrics.heightPixels
        }
    }


    override fun onStop() {
        super.onStop()
        exoPlayer.pause()
    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}