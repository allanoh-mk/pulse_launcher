package app.lawnchair.pulse.fontstudio

import androidx.compose.ui.text.font.FontFamily

/**
 * Font Studio's selectable families. Scoped to bundled system font families
 * for now (deterministic, no network, no device-specific Google Fonts
 * provider certificate wiring to verify without a physical device — see
 * docs/codebase/IMPLEMENTATION_PLAN.md Phase 3 notes). Swapping in live
 * Google Fonts later only requires adding entries here backed by
 * `androidx.compose.ui.text.googlefonts.Font`.
 */
enum class PulseFontOption(val displayName: String, val fontFamily: FontFamily) {
    SYSTEM_DEFAULT("System Default", FontFamily.Default),
    SANS_SERIF("Sans Serif", FontFamily.SansSerif),
    SERIF("Serif", FontFamily.Serif),
    MONOSPACE("Monospace", FontFamily.Monospace),
    CURSIVE("Cursive", FontFamily.Cursive),
    ;

    companion object {
        fun fromName(name: String): PulseFontOption =
            entries.firstOrNull { it.name == name } ?: SYSTEM_DEFAULT
    }
}
