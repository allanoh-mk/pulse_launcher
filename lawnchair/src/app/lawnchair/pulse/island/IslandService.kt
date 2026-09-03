package app.lawnchair.pulse.island

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import app.lawnchair.pulse.core.PulsePreferences
import com.android.launcher3.R
import com.patrykmichalik.opto.core.firstBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Persistent foreground overlay that renders the Dynamic Island. Requires:
 *  - "Draw over other apps" (SYSTEM_ALERT_WINDOW) — must be granted by the
 *    user from system settings; there is no runtime prompt for it.
 *  - Notification access already granted to Lawnchair's existing listener
 *    (reused for media-session lookups), see [app.lawnchair.pulse.notifications.PulseNotifications].
 *
 * Because there is no attached device in this build environment, the window
 * placement/animation behavior described here is compile-verified only and
 * should be confirmed visually on a real phone before relying on it.
 */
class IslandService :
    Service(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
    private val userRequestedExpand = MutableStateFlow(false)

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        if (!PulsePreferences.getInstance(this).dynamicIslandEnabled.firstBlocking()) {
            stopSelf()
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            // Cannot draw the overlay without this permission; the settings
            // screen is responsible for directing the user to grant it.
            stopSelf()
            return
        }

        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        attachOverlay()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    private fun attachOverlay() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager = wm

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
            y = (if (statusBarHeightId > 0) resources.getDimensionPixelSize(statusBarHeightId) else 0)
                .coerceAtLeast(24)
        }

        val notificationListenerComponent =
            ComponentName(this, "com.android.launcher3.notification.NotificationListener")
        val mediaActivityFlow = MediaSessionObserver.observe(this, notificationListenerComponent)

        val view = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(this@IslandService)
            setViewTreeViewModelStoreOwner(this@IslandService)
            setViewTreeSavedStateRegistryOwner(this@IslandService)
        }
        overlayView = view
        wm.addView(view, params)

        view.setContent {
            val activitiesState = combine(mediaActivityFlow, userRequestedExpand) { media, expand ->
                Pair(listOfNotNull(media), expand)
            }.collectAsState(Pair(emptyList<IslandActivity>(), false))

            val activityList = activitiesState.value.first
            val expand = activitiesState.value.second
            val visualState = IslandStateMachine.resolve(activityList, expand)
            IslandOverlay(
                state = visualState,
                onTap = { userRequestedExpand.value = !userRequestedExpand.value },
                onDismiss = { userRequestedExpand.value = false },
            )
        }
    }

    private fun buildForegroundNotification(): Notification {
        val channelId = "pulse_island_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Dynamic Island",
                AndroidNotificationManager.IMPORTANCE_MIN,
            )
            getSystemService(AndroidNotificationManager::class.java).createNotificationChannel(channel)
        }
        return Notification.Builder(this, channelId)
            .setContentTitle("Pulse Dynamic Island")
            .setSmallIcon(R.drawable.ic_launcher_home)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        overlayView?.let { view -> runCatching { windowManager?.removeView(view) } }
        serviceScope.launch { }.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 4271

        fun start(context: Context) {
            context.startForegroundService(Intent(context, IslandService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, IslandService::class.java))
        }
    }
}

private fun <T> combine(
    a: kotlinx.coroutines.flow.Flow<IslandActivity?>,
    b: kotlinx.coroutines.flow.Flow<Boolean>,
    transform: (IslandActivity?, Boolean) -> T,
): kotlinx.coroutines.flow.Flow<T> = a.combine(b, transform)

@androidx.compose.runtime.Composable
private fun <T> kotlinx.coroutines.flow.Flow<T>.collectAsComposeState(initial: T) =
    this.collectAsState(initial = initial)
