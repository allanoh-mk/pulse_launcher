package app.lawnchair.pulse.notifications

import android.content.Context
import android.service.notification.StatusBarNotification
import app.lawnchair.NotificationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class PulseNotification(
    val key: String,
    val packageName: String,
    val title: String?,
    val text: String?,
    val postTimeMillis: Long,
)

/**
 * Thin adapter over Lawnchair's existing [NotificationManager] (which already
 * owns the bound [com.android.launcher3.notification.NotificationListener]
 * service and the "Notification access" permission grant). Pulse never binds
 * its own listener — that would create a second, confusing permission entry
 * for the same capability.
 */
object PulseNotifications {

    fun observe(context: Context): Flow<List<PulseNotification>> =
        NotificationManager.INSTANCE.get(context).notifications.map { sbnList ->
            sbnList.mapNotNull(::toPulseNotification).sortedByDescending { it.postTimeMillis }
        }

    private fun toPulseNotification(sbn: StatusBarNotification): PulseNotification? {
        val extras = sbn.notification?.extras ?: return null
        return PulseNotification(
            key = sbn.key,
            packageName = sbn.packageName,
            title = extras.getCharSequence("android.title")?.toString(),
            text = extras.getCharSequence("android.text")?.toString(),
            postTimeMillis = sbn.postTime,
        )
    }
}
