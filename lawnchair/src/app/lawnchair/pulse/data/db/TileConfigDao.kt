package app.lawnchair.pulse.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TileConfigDao {
    @Query("SELECT * FROM pulse_tiles ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<TileConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tile: TileConfig)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tiles: List<TileConfig>)

    @Update
    suspend fun update(tile: TileConfig)

    @Delete
    suspend fun delete(tile: TileConfig)

    @Query("DELETE FROM pulse_tiles WHERE id = :id")
    suspend fun deleteById(id: String)
}
