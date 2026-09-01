package app.lawnchair.pulse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AiProviderSettingDao {
    @Query("SELECT * FROM pulse_ai_providers")
    fun observeAll(): Flow<List<AiProviderSetting>>

    @Query("SELECT * FROM pulse_ai_providers WHERE providerId = :providerId LIMIT 1")
    suspend fun get(providerId: String): AiProviderSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: AiProviderSetting)
}
