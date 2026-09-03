package app.lawnchair.pulse.music

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import app.lawnchair.pulse.music.model.PulseTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PulseMediaScanner {

    private val DEMO_TRACKS = listOf(
        PulseTrack(
            id = "demo_1",
            title = "Midnight Horizon",
            artist = "Pulse Lo-Fi",
            album = "Neon Drift",
            durationMs = 184000L,
            coverUri = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            isOnline = true,
        ),
        PulseTrack(
            id = "demo_2",
            title = "Cosmic Reverie",
            artist = "Astral Pulse",
            album = "Starlight Frequencies",
            durationMs = 212000L,
            coverUri = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=500&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            isOnline = true,
        ),
        PulseTrack(
            id = "demo_3",
            title = "Tokyo Rain",
            artist = "SynthWave Echo",
            album = "Night City",
            durationMs = 196000L,
            coverUri = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            isOnline = true,
        ),
    )

    suspend fun scanLocalTracks(context: Context): List<PulseTrack> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<PulseTrack>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder,
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val title = it.getString(titleCol) ?: "Unknown Track"
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val album = it.getString(albumCol) ?: ""
                    val durationMs = it.getLong(durationCol)
                    val albumId = it.getLong(albumIdCol)

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id,
                    )
                    val albumArtUri = "content://media/external/audio/albumart/$albumId"

                    tracks.add(
                        PulseTrack(
                            id = id.toString(),
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = durationMs,
                            coverUri = albumArtUri,
                            mediaUri = contentUri.toString(),
                            isOnline = false,
                        ),
                    )
                }
            }
        } catch (e: Exception) {
            // Permission or content resolver issue; graceful fallback
        }

        if (tracks.isEmpty()) {
            DEMO_TRACKS
        } else {
            tracks
        }
    }

    fun getDemoTracks(): List<PulseTrack> = DEMO_TRACKS
}
