package app.lawnchair.pulse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusModeConfigDao {
    @Query("SELECT * FROM pulse_focus_modes")
    fun observeAll(): Flow<List<FocusModeConfig>>

    @Query("SELECT * FROM pulse_focus_modes WHERE isActive = 1 LIMIT 1")
    suspend fun getActive(): FocusModeConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: FocusModeConfig)

    @Query("UPDATE pulse_focus_modes SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE pulse_focus_modes SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: String)

    @Query("DELETE FROM pulse_focus_modes WHERE id = :id")
    suspend fun delete(id: String)
}
