package app.lawnchair.pulse.core

import android.app.ActivityManager
import android.content.Context
import android.os.Build

/**
 * Detects device performance class so Pulse can gate expensive visual effects
 * (real-time blur, spring-heavy animations, live island shimmer) on Android Go /
 * low-RAM hardware while keeping them on for capable Android 12+ devices.
 *
 * Pure logic (no framework calls) is isolated into companion functions so it is
 * unit-testable without Robolectric/instrumentation.
 */
object DeviceCapabilities {

    /** Android Go Edition ships on devices this constrained or lower. */
    const val ANDROID_GO_RAM_THRESHOLD_MB = 2_048

    data class Profile(
        val isLowRamDevice: Boolean,
        val isGoEdition: Boolean,
        val totalRamMb: Long,
        val supportsRealtimeBlur: Boolean,
    )

    fun current(context: Context): Profile {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        val totalRamMb = memoryInfo.totalMem / (1024 * 1024)
        val isLowRam = activityManager?.isLowRamDevice == true
        return profileFor(
            totalRamMb = totalRamMb,
            isLowRamDeviceFlag = isLowRam,
            sdkInt = Build.VERSION.SDK_INT,
        )
    }

    /**
     * Pure decision function, extracted so it can be unit tested without
     * touching [ActivityManager] or other Android framework classes.
     */
    fun profileFor(totalRamMb: Long, isLowRamDeviceFlag: Boolean, sdkInt: Int): Profile {
        val isGoEdition = isLowRamDeviceFlag || totalRamMb in 1..ANDROID_GO_RAM_THRESHOLD_MB.toLong()
        val supportsRealtimeBlur = !isGoEdition && sdkInt >= Build.VERSION_CODES.S
        return Profile(
            isLowRamDevice = isLowRamDeviceFlag,
            isGoEdition = isGoEdition,
            totalRamMb = totalRamMb,
            supportsRealtimeBlur = supportsRealtimeBlur,
        )
    }
}
