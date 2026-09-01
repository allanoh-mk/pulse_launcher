package app.lawnchair.pulse.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.launcher3.util.MainThreadInitializedObject
import com.patrykmichalik.opto.core.PreferenceManager
import com.patrykmichalik.opto.core.firstBlocking

/**
 * Feature-flag layer for every optional / heavy Pulse feature.
 *
 * Design rule (explicit product decision): anything that costs battery, does
 * network I/O, or is still experimental defaults to OFF. The user opts in from
 * Settings > Pulse. Core layout features (workspace slides, control center)
 * default ON since they are the reason the launcher exists, but remain
 * toggleable so the launcher can be fully "stock-like" if desired.
 */
class PulsePreferences private constructor(context: Context) : PreferenceManager {

    override val preferencesDataStore = context.pulsePreferencesDataStore

    /** Master switch for the on-device/remote LLM assistant. OFF by default. */
    val assistantEnabled = preference(
        key = booleanPreferencesKey("assistant_enabled"),
        defaultValue = false,
    )

    /** Which provider the assistant should talk to when enabled. */
    val assistantProvider = preference(
        key = stringPreferencesKey("assistant_provider"),
        defaultValue = "OLLAMA",
    )

    /** Dynamic Island overlay. ON by default (core feature) but user-toggleable. */
    val dynamicIslandEnabled = preference(
        key = booleanPreferencesKey("dynamic_island_enabled"),
        defaultValue = true,
    )

    /** Custom Control Center overlay. ON by default. */
    val controlCenterEnabled = preference(
        key = booleanPreferencesKey("control_center_enabled"),
        defaultValue = true,
    )

    /** Unified search overlay replacing the stock QSB flow. ON by default. */
    val unifiedSearchEnabled = preference(
        key = booleanPreferencesKey("unified_search_enabled"),
        defaultValue = true,
    )

    /**
     * Real-time frosted-glass blur behind overlays. Defaults to OFF on Go/low-RAM
     * devices and ON otherwise; the stored value is a user override on top of
     * that automatic decision (null-equivalent handled by [resolvedBlurEnabled]).
     */
    val forceDisableBlur = preference(
        key = booleanPreferencesKey("force_disable_blur"),
        defaultValue = false,
    )

    /** Icon Studio custom render pipeline (shape mask + specular + edge). ON by default. */
    val iconStudioEnabled = preference(
        key = booleanPreferencesKey("icon_studio_enabled"),
        defaultValue = true,
    )

    /** Voice wake word listening (Porcupine or similar). OFF by default: mic-always-on is opt-in. */
    val voiceWakeWordEnabled = preference(
        key = booleanPreferencesKey("voice_wake_word_enabled"),
        defaultValue = false,
    )

    /** Font Studio selection. Stored as a [app.lawnchair.pulse.fontstudio.PulseFontOption] name. */
    val fontOption = preference(
        key = stringPreferencesKey("pulse_font_option"),
        defaultValue = "SYSTEM_DEFAULT",
    )

    fun resolvedBlurEnabled(deviceProfile: DeviceCapabilities.Profile): Boolean =
        deviceProfile.supportsRealtimeBlur && !forceDisableBlur.firstBlocking()

    companion object {
        private val Context.pulsePreferencesDataStore by preferencesDataStore(name = "pulse_preferences")

        @JvmField
        val INSTANCE = MainThreadInitializedObject(::PulsePreferences)

        @JvmStatic
        fun getInstance(context: Context): PulsePreferences = INSTANCE.get(context)!!
    }
}

@Composable
fun pulsePreferences() = PulsePreferences.getInstance(LocalContext.current)
