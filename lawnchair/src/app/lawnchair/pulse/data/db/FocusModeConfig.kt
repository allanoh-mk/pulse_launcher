package app.lawnchair.pulse.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pulse_focus_modes")
data class FocusModeConfig(
    @PrimaryKey val id: String,
    val name: String,
    val accentColorArgb: Int,
    val hiddenPackages: List<String>,
    val isActive: Boolean = false,
)
