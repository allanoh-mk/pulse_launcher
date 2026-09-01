package app.lawnchair.pulse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IconStyleConfigDao {
    @Query("SELECT * FROM pulse_icon_styles")
    fun observeAll(): Flow<List<IconStyleConfig>>

    @Query("SELECT * FROM pulse_icon_styles WHERE packageName = :packageName LIMIT 1")
    suspend fun get(packageName: String): IconStyleConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: IconStyleConfig)

    @Query("DELETE FROM pulse_icon_styles WHERE packageName = :packageName")
    suspend fun delete(packageName: String)
}
