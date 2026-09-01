package app.lawnchair.pulse.workspace

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.UserHandle
import android.os.Process
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

data class PulseAppInfo(
    val packageName: String,
    val label: String,
    val user: UserHandle,
    val icon: Drawable,
)

/**
 * Minimal installed-app source for the Pulse workspace slides (Tile grid and
 * vertical list). Deliberately independent from Launcher3's [com.android.launcher3.model.BgDataModel]
 * so the Compose slides can be developed/tested without depending on the full
 * loader pipeline; it uses the same underlying [LauncherApps] API Launcher3 itself
 * uses for enumeration.
 */
object AppListProvider {

    fun observeApps(context: Context): Flow<List<PulseAppInfo>> = callbackFlow {
        val launcherApps = context.getSystemService(LauncherApps::class.java)
        val appContext = context.applicationContext

        fun snapshot(): List<PulseAppInfo> {
            val user = Process.myUserHandle()
            val activities: List<LauncherActivityInfo> = launcherApps.getActivityList(null, user)
            return activities.map { info ->
                PulseAppInfo(
                    packageName = info.applicationInfo.packageName,
                    label = info.label.toString(),
                    user = info.user,
                    icon = info.getIcon(0),
                )
            }.sortedBy { it.label.lowercase() }
        }

        trySend(snapshot())

        val callback = object : LauncherApps.Callback() {
            override fun onPackageRemoved(packageName: String?, user: UserHandle?) = refresh()
            override fun onPackageAdded(packageName: String?, user: UserHandle?) = refresh()
            override fun onPackageChanged(packageName: String?, user: UserHandle?) = refresh()
            override fun onPackagesAvailable(packageNames: Array<out String>?, user: UserHandle?, replacing: Boolean) =
                refresh()

            override fun onPackagesUnavailable(
                packageNames: Array<out String>?,
                user: UserHandle?,
                replacing: Boolean
            ) = refresh()

            private fun refresh() {
                trySend(snapshot())
            }
        }
        launcherApps.registerCallback(callback)

        awaitClose { launcherApps.unregisterCallback(callback) }
    }

    /** Groups apps by their first display letter for the vertical Wave Alphabet list (Slide 3). */
    fun Flow<List<PulseAppInfo>>.groupedByFirstLetter(): Flow<Map<Char, List<PulseAppInfo>>> = map { apps ->
        apps.groupBy { app ->
            app.label.firstOrNull()?.uppercaseChar()?.takeIf { it.isLetter() } ?: '#'
        }.toSortedMap()
    }
}
