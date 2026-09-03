package app.lawnchair.pulse.music

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import app.lawnchair.pulse.music.model.PulsePlaybackState
import app.lawnchair.pulse.music.model.PulseTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object PulseMusicEngine {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _state = MutableStateFlow(PulsePlaybackState())
    val state: StateFlow<PulsePlaybackState> = _state.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSession? = null
    private var progressJob: Job? = null
    private var appContext: Context? = null

    fun initialize(context: Context) {
        if (appContext != null) return
        appContext = context.applicationContext

        runCatching {
            mediaSession = MediaSession(context, "PulseMusicEngine").apply {
                setCallback(object : MediaSession.Callback() {
                    override fun onPlay() {
                        resume()
                    }
                    override fun onPause() {
                        pause()
                    }
                    override fun onSkipToNext() {
                        next()
                    }
                    override fun onSkipToPrevious() {
                        previous()
                    }
                    override fun onSeekTo(pos: Long) {
                        seekTo(pos)
                    }
                })
                isActive = true
            }
        }
    }

    fun playQueue(tracks: List<PulseTrack>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        val validIndex = startIndex.coerceIn(0, tracks.lastIndex)
        _state.update {
            it.copy(
                queue = tracks,
                queueIndex = validIndex,
            )
        }
        playTrack(tracks[validIndex])
    }

    fun playTrack(track: PulseTrack) {
        stopProgressTracker()
        releasePlayer()

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build(),
                )

                val context = appContext
                if (context != null && !track.isOnline && track.mediaUri.startsWith("content://")) {
                    setDataSource(context, Uri.parse(track.mediaUri))
                } else if (track.mediaUri.isNotEmpty()) {
                    setDataSource(track.mediaUri)
                }

                setOnPreparedListener { mp ->
                    mp.start()
                    val actualDuration = if (mp.duration > 0) mp.duration.toLong() else track.durationMs
                    _state.update {
                        it.copy(
                            currentTrack = track,
                            isPlaying = true,
                            positionMs = 0L,
                            durationMs = actualDuration,
                        )
                    }
                    startProgressTracker()
                    updateMediaSessionState(PlaybackState.STATE_PLAYING, 0L, track)
                }

                setOnCompletionListener {
                    handleTrackCompletion()
                }

                setOnErrorListener { _, _, _ ->
                    _state.update { it.copy(isPlaying = false) }
                    stopProgressTracker()
                    true
                }

                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            // Fallback simulation in case of unsupported media or emulator without audio device
            simulatePlayback(track)
        }
    }

    private fun simulatePlayback(track: PulseTrack) {
        val duration = if (track.durationMs > 0) track.durationMs else 180000L
        _state.update {
            it.copy(
                currentTrack = track,
                isPlaying = true,
                positionMs = 0L,
                durationMs = duration,
            )
        }
        startProgressTracker()
        updateMediaSessionState(PlaybackState.STATE_PLAYING, 0L, track)
    }

    fun togglePlayPause() {
        val current = _state.value
        if (current.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        runCatching { mediaPlayer?.pause() }
        _state.update { it.copy(isPlaying = false) }
        stopProgressTracker()
        updateMediaSessionState(PlaybackState.STATE_PAUSED, _state.value.positionMs, _state.value.currentTrack)
    }

    fun resume() {
        if (_state.value.currentTrack == null) {
            val queue = _state.value.queue
            if (queue.isNotEmpty()) {
                playTrack(queue[_state.value.queueIndex])
            }
            return
        }

        if (mediaPlayer != null) {
            runCatching { mediaPlayer?.start() }
        }
        _state.update { it.copy(isPlaying = true) }
        startProgressTracker()
        updateMediaSessionState(PlaybackState.STATE_PLAYING, _state.value.positionMs, _state.value.currentTrack)
    }

    fun next() {
        val s = _state.value
        if (s.queue.isEmpty()) return

        val nextIndex = if (s.isShuffle) {
            (0 until s.queue.size).random()
        } else {
            (s.queueIndex + 1) % s.queue.size
        }

        _state.update { it.copy(queueIndex = nextIndex) }
        playTrack(s.queue[nextIndex])
    }

    fun previous() {
        val s = _state.value
        if (s.queue.isEmpty()) return

        if (s.positionMs > 3000L) {
            seekTo(0L)
            return
        }

        val prevIndex = if (s.queueIndex - 1 < 0) s.queue.lastIndex else s.queueIndex - 1
        _state.update { it.copy(queueIndex = prevIndex) }
        playTrack(s.queue[prevIndex])
    }

    fun seekTo(positionMs: Long) {
        val safePos = positionMs.coerceIn(0L, _state.value.durationMs)
        runCatching { mediaPlayer?.seekTo(safePos.toInt()) }
        _state.update { it.copy(positionMs = safePos) }
        val stateConst = if (_state.value.isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED
        updateMediaSessionState(stateConst, safePos, _state.value.currentTrack)
    }

    fun toggleShuffle() {
        _state.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun cycleRepeatMode() {
        _state.update {
            val nextMode = when (it.repeatMode) {
                PulsePlaybackState.RepeatMode.OFF -> PulsePlaybackState.RepeatMode.ALL
                PulsePlaybackState.RepeatMode.ALL -> PulsePlaybackState.RepeatMode.ONE
                PulsePlaybackState.RepeatMode.ONE -> PulsePlaybackState.RepeatMode.OFF
            }
            it.copy(repeatMode = nextMode)
        }
    }

    private fun handleTrackCompletion() {
        when (_state.value.repeatMode) {
            PulsePlaybackState.RepeatMode.ONE -> {
                seekTo(0L)
                resume()
            }
            PulsePlaybackState.RepeatMode.ALL, PulsePlaybackState.RepeatMode.OFF -> {
                val s = _state.value
                if (s.repeatMode == PulsePlaybackState.RepeatMode.OFF && s.queueIndex >= s.queue.lastIndex) {
                    pause()
                    seekTo(0L)
                } else {
                    next()
                }
            }
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive && _state.value.isPlaying) {
                val pos = runCatching { mediaPlayer?.currentPosition?.toLong() }.getOrNull()
                if (pos != null && pos > 0) {
                    _state.update { it.copy(positionMs = pos) }
                } else {
                    // Manual ticker progression if running in simulated mode
                    _state.update {
                        val newPos = (it.positionMs + 200L).coerceAtMost(it.durationMs)
                        if (newPos >= it.durationMs) {
                            handleTrackCompletion()
                        }
                        it.copy(positionMs = newPos)
                    }
                }
                delay(200L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    private fun updateMediaSessionState(state: Int, positionMs: Long, track: PulseTrack?) {
        val session = mediaSession ?: return
        val playbackState = PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_PLAY or
                    PlaybackState.ACTION_PAUSE or
                    PlaybackState.ACTION_SKIP_TO_NEXT or
                    PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackState.ACTION_SEEK_TO,
            )
            .setState(state, positionMs, 1.0f)
            .build()
        session.setPlaybackState(playbackState)

        if (track != null) {
            val metadata = MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, track.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, track.album)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, track.durationMs)
                .build()
            session.setMetadata(metadata)
        }
    }
}
