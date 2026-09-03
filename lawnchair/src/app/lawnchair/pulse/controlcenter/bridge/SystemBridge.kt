package app.lawnchair.pulse.controlcenter.bridge

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * Modern wrapper for system toggles that avoids deprecated APIs (like WifiManager.isWifiEnabled).
 */
class SystemBridge(private val context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null

    init {
        try {
            val idList = cameraManager.cameraIdList
            if (idList.isNotEmpty()) {
                cameraId = idList[0]
            }
        } catch (e: Exception) {
            Log.e("SystemBridge", "Failed to get camera id", e)
        }
    }

    /**
     * Toggles flashlight. Requires CAMERA permission.
     */
    fun setFlashlight(enabled: Boolean) {
        try {
            cameraId?.let {
                cameraManager.setTorchMode(it, enabled)
            }
        } catch (e: Exception) {
            Log.e("SystemBridge", "Failed to toggle flashlight", e)
        }
    }

    /**
     * Opens the modern Settings panel for Wi-Fi on Android Q+.
     * Starting in API 29, apps cannot toggle Wi-Fi directly without being system apps.
     */
    fun openWifiPanel() {
        val intent = Intent(Settings.Panel.ACTION_WIFI)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback for devices without the panel
            val fallback = Intent(Settings.ACTION_WIFI_SETTINGS)
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(fallback)
        }
    }

    /**
     * Opens the modern Settings panel for Internet connectivity (which includes cellular/Wi-Fi).
     */
    fun openInternetPanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                openWifiPanel()
            }
        } else {
            openWifiPanel()
        }
    }

    fun toggleAutoRotate(): Boolean {
        return try {
            val current = Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0)
            val newState = if (current == 1) 0 else 1
            if (Settings.System.canWrite(context)) {
                Settings.System.putInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, newState)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isAutoRotateEnabled(): Boolean {
        return try {
            Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
        } catch (e: Exception) {
            false
        }
    }
}
