package app.lawnchair.pulse.core

import android.os.Build
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceCapabilitiesTest {

    @Test
    fun `low ram flag always marks device as go edition`() {
        val profile = DeviceCapabilities.profileFor(
            totalRamMb = 6_000,
            isLowRamDeviceFlag = true,
            sdkInt = Build.VERSION_CODES.TIRAMISU,
        )
        assertTrue(profile.isGoEdition)
        assertFalse(profile.supportsRealtimeBlur)
    }

    @Test
    fun `ram at or below threshold is go edition even without the flag`() {
        val profile = DeviceCapabilities.profileFor(
            totalRamMb = DeviceCapabilities.ANDROID_GO_RAM_THRESHOLD_MB.toLong(),
            isLowRamDeviceFlag = false,
            sdkInt = Build.VERSION_CODES.S,
        )
        assertTrue(profile.isGoEdition)
    }

    @Test
    fun `capable device on Android 12 plus supports realtime blur`() {
        val profile = DeviceCapabilities.profileFor(
            totalRamMb = 8_192,
            isLowRamDeviceFlag = false,
            sdkInt = Build.VERSION_CODES.S,
        )
        assertFalse(profile.isGoEdition)
        assertTrue(profile.supportsRealtimeBlur)
    }

    @Test
    fun `capable ram but pre-Android 12 does not support realtime blur`() {
        val profile = DeviceCapabilities.profileFor(
            totalRamMb = 8_192,
            isLowRamDeviceFlag = false,
            sdkInt = Build.VERSION_CODES.R,
        )
        assertFalse(profile.supportsRealtimeBlur)
    }
}
