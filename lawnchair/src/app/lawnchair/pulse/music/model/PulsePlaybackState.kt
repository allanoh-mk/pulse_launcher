package app.lawnchair.pulse.music.model

data class PulsePlaybackState(
    val currentTrack: PulseTrack? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val queue: List<PulseTrack> = emptyList(),
    val queueIndex: Int = 0,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
) {
    enum class RepeatMode { OFF, ALL, ONE }

    val progressFraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
