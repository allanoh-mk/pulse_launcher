package app.lawnchair.pulse.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Per-app (or global, when [packageName] is null) Icon Studio override.
 * A single global row with packageName == null represents the default style
 * applied to every icon that has no explicit per-app row.
 */
@Entity(tableName = "pulse_icon_styles")
data class IconStyleConfig(
    @PrimaryKey val packageName: String,
    val shape: String,
    val style: String,
    val sizeScale: Float = 1f,
    val customLabel: String? = null,
)

const val GLOBAL_ICON_STYLE_KEY = "__global__"
