package app.lawnchair.pulse.island

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Wraps [MediaSessionManager] active-session callbacks (bound via the same
 * notification-listener component Lawnchair already declares) into an
 * [IslandActivity] stream for [IslandStateMachine].
 */
object MediaSessionObserver {

    fun observe(context: Context, notificationListenerComponent: ComponentName): Flow<IslandActivity?> = callbackFlow {
        val manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

        fun toIslandActivity(controllers: List<MediaController>?): IslandActivity? {
            val controller = controllers?.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
                ?: controllers?.firstOrNull()
                ?: return null
            val metadata = controller.metadata ?: return null
            val title = metadata.getString(android.media.MediaMetadata.METADATA_KEY_TITLE) ?: return null
            val artist = metadata.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST)
            return IslandActivity(
                id = "media:${controller.packageName}",
                type = IslandActivityType.MEDIA,
                title = title,
                subtitle = artist,
                iconPackageName = controller.packageName,
            )
        }

        val listener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
            trySend(toIslandActivity(controllers))
        }

        val initial = runCatching {
            manager.getActiveSessions(notificationListenerComponent)
        }.getOrNull()
        trySend(toIslandActivity(initial))

        runCatching {
            manager.addOnActiveSessionsChangedListener(listener, notificationListenerComponent)
        }

        awaitClose {
            runCatching { manager.removeOnActiveSessionsChangedListener(listener) }
        }
    }
}
