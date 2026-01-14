package com.example.toobo2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.toobo2.data.local.entity.SavedLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM saved_locations WHERE isCurrentLocation = 0 ORDER BY name ASC")
    fun getSavedLocations(): Flow<List<SavedLocationEntity>>

    @Query("SELECT * FROM saved_locations WHERE isCurrentLocation = 1 LIMIT 1")
    fun getCurrentLocation(): Flow<SavedLocationEntity?>

    @Query("SELECT * FROM saved_locations WHERE isCurrentLocation = 1 LIMIT 1")
    suspend fun getCurrentLocationOnce(): SavedLocationEntity?

    @Query("SELECT * FROM saved_locations WHERE id = :id")
    suspend fun getLocationById(id: Long): SavedLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SavedLocationEntity): Long

    @Update
    suspend fun updateLocation(location: SavedLocationEntity)

    @Delete
    suspend fun deleteLocation(location: SavedLocationEntity)

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteLocationById(id: Long)

    @Query("DELETE FROM saved_locations WHERE isCurrentLocation = 1")
    suspend fun deleteCurrentLocation()

    @Query("SELECT EXISTS(SELECT 1 FROM saved_locations WHERE latitude = :lat AND longitude = :lon AND isCurrentLocation = 0)")
    suspend fun isLocationSaved(lat: Double, lon: Double): Boolean
}
