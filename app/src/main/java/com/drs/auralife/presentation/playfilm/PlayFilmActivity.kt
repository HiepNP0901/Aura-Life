package com.drs.auralife.presentation.playfilm

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.presentation.filmdetails.FilmDetailsViewModel
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.presentation.history.HistoryViewModel
import com.drs.auralife.presentation.auth.LoginActivity
import com.drs.auralife.presentation.filmdetails.EXTRA_SLUG
import com.drs.auralife.presentation.payment.PaymentActivity
import com.drs.auralife.core.utils.SystemUiController
import javax.inject.Inject
import kotlinx.coroutines.launch

@dagger.hilt.android.AndroidEntryPoint
class PlayFilmActivity : AppCompatActivity() {
    private val filmDetailsViewModel: FilmDetailsViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val playFilmViewModel: PlayFilmViewModel by viewModels()
    private var recyclerView: RecyclerView? = null

    @Inject
    lateinit var authRepository: AuthRepository

    private var exoPlayer: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var nameFilm: TextView? = null

    private var btnPrevious: ImageButton? = null
    private var btnRewind: ImageButton? = null
    private var btnPlayPause: ImageButton? = null
    private var btnForward: ImageButton? = null
    private var btnNext: ImageButton? = null

    private var fullscreenButton: ImageButton? = null
    private var rotateButton: ImageButton? = null

    private var numberEpInLine = DEFAULT_EPISODES_PER_LINE
    private var currentEpisode = 0
    private var currentPosition: Long = 0
    private var isFullscreen = false
    private var slug: String? = null
    private var film: FilmDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_film)

        slug = intent.getStringExtra(EXTRA_SLUG)

        exoPlayer = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.player_view)
        playerView?.apply {
            player = exoPlayer

            btnPlayPause = findViewById(R.id.btn_play_pause)
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

        observeFilmDetails()

        slug?.let { slug ->
            lifecycleScope.launch {
                historyViewModel.getHistoryItem(slug)?.let {
                    currentEpisode = it.episode
                    currentPosition = it.position
                }
                filmDetailsViewModel.getFilmDetails(slug)
            }
        }

        startPlaybackMonitor()

        settingExoPlayer()
    }

    private fun observeFilmDetails() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                filmDetailsViewModel.filmDetailsState.collect { details ->
                    details?.let {
                        film = it
                        playEpisode(currentEpisode)
                        recyclerView?.adapter = EpisodeAdapter(it.episodes) { ep ->
                            playEpisode(ep)
                        }
                    }
                }
            }
        }

        playFilmViewModel.loadPremiumStatus()
        observePlaybackThrottle()
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
        currentPosition = (savedInstanceState.getLong("exoPlayerPosition", 0) - POSITION_OFFSET_MS).coerceAtLeast(0L)
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.apply {
            historyViewModel.addToHistory(slug.toString(), currentEpisode, currentPosition)
            pause()
            btnPlayPause?.isSelected = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }

    private fun playEpisode(episodeIndex: Int) {
        film?.let { filmDetails ->
            if (episodeIndex in filmDetails.episodes.indices) {
                val episode = filmDetails.episodes[episodeIndex]
                exoPlayer?.setMediaItem(MediaItem.fromUri(episode.linkM3u8))

                exoPlayer?.prepare()
                exoPlayer?.seekTo(currentPosition)
                exoPlayer?.play()

                nameFilm?.text = episode.filename
                if (currentEpisode != episodeIndex) {
                    currentPosition = 0
                }
                currentEpisode = episodeIndex
                btnPlayPause?.isSelected = true
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

            btnPlayPause?.isSelected = true

            btnPlayPause?.setOnClickListener {
                if (isPlaying == true) {
                    pause()
                    btnPlayPause?.isSelected = false
                } else {
                    play()
                    btnPlayPause?.isSelected = true
                }
            }

            btnRewind?.setOnClickListener {
                val rewindPosition = currentPosition - REWIND_MS
                exoPlayer?.seekTo(rewindPosition.coerceAtLeast(0))
            }

            btnForward?.setOnClickListener {
                val forwardPosition = currentPosition + FORWARD_MS
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
                layoutParams.height = dpToPx(PLAYER_HEIGHT_DP, resources.displayMetrics).toInt()
            } else {
                SystemUiController.autoHideSystemUI(window)
                otherViews.forEach { it?.visibility = View.GONE }
                layoutParams.height = resources.displayMetrics.heightPixels
            }
        }
    }

    private fun startPlaybackMonitor() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    exoPlayer?.let { player ->
                        playFilmViewModel.checkPlaybackThrottle(
                            position = player.currentPosition,
                            maxPreviewDurationMs = FREE_PREVIEW_LIMIT_MS,
                        )
                    }
                    kotlinx.coroutines.delay(PLAYBACK_CHECK_INTERVAL_MS)
                }
            }
        }
    }

    private fun observePlaybackThrottle() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                playFilmViewModel.throttleEvent.collect {
                    exoPlayer?.pause()
                    btnPlayPause?.isSelected = false
                    exoPlayer?.seekTo(FREE_PREVIEW_LIMIT_MS - POSITION_OFFSET_MS)
                    showPremiumDialog()
                }
            }
        }
    }

    private fun showPremiumDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null)
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
            if (authRepository.isLoggedIn()) {
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        dialog.show()
    }

    companion object {
        private const val DEFAULT_EPISODES_PER_LINE = 3
        private const val PLAYBACK_INITIAL_DELAY_MS = 1000L
        private const val PLAYBACK_CHECK_INTERVAL_MS = 1000L
        private const val POSITION_OFFSET_MS = 1000L
        private const val REWIND_MS = 5000L
        private const val FORWARD_MS = 15000L
        private const val PLAYER_HEIGHT_DP = 250f
        private const val FREE_PREVIEW_LIMIT_MS = 5L * 60 * 1000
    }
}
