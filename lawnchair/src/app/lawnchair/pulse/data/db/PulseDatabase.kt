package app.lawnchair.pulse.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.launcher3.util.MainThreadInitializedObject

@Database(
    entities = [
        TileConfig::class,
        IconStyleConfig::class,
        FocusModeConfig::class,
        AiProviderSetting::class,
    ],
    version = 1,
)
@TypeConverters(PulseConverters::class)
abstract class PulseDatabase : RoomDatabase() {

    abstract fun tileConfigDao(): TileConfigDao
    abstract fun iconStyleConfigDao(): IconStyleConfigDao
    abstract fun focusModeConfigDao(): FocusModeConfigDao
    abstract fun aiProviderSettingDao(): AiProviderSettingDao

    companion object {
        // Personal single-user app: destructive fallback is an explicit, accepted
        // tradeoff (see docs/codebase/CONCERNS.md) since there is no multi-device
        // sync to preserve and schema churn is expected during active development.
        val INSTANCE = MainThreadInitializedObject { context ->
            Room.databaseBuilder(
                context,
                PulseDatabase::class.java,
                "pulse.db",
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
