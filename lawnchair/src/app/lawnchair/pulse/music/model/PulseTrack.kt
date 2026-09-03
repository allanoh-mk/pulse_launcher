package app.lawnchair.pulse.music.model

data class PulseTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationMs: Long = 0L,
    val coverUri: String? = null,
    val mediaUri: String = "",
    val isOnline: Boolean = false,
)
