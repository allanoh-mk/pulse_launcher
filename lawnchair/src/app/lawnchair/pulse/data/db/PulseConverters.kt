package app.lawnchair.pulse.data.db

import androidx.room.TypeConverter

class PulseConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(separator = "\u001F")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split("\u001F")
}
