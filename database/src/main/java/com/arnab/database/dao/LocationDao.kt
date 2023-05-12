package com.arnab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arnab.database.entity.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationEntity: LocationEntity)

    @Query("SELECT * FROM LocationTable WHERE id = :id")
    suspend fun selectLocationById(id: Long): LocationEntity

    @Query("SELECT * FROM LocationTable WHERE name = :name")
    suspend fun retrieveLocationByName(name: String): LocationEntity

    @Query("SELECT * FROM LocationTable WHERE 1 = 1")
    suspend fun retrieveAllLocations(): List<LocationEntity>

    @Query("DELETE FROM LocationTable WHERE name = :name")
    suspend fun deleteLocationByName(name: String): Int

    @Query("DELETE FROM LocationTable WHERE 1 = 1")
    suspend fun deleteAllLocations(): Int


    //region retrieveLastLocation
    // Equivalent SQL QUERY
    // "SELECT * FROM LocationTable WHERE lastUpdateTimeStamp < SELECT MAX(lastUpdateTimeStamp FROM LocationTable"
    @Transaction
    suspend fun retrieveLastLocation(): LocationEntity {
        val timeStamp = retrieveLastUpdateTimeStamp()
        return retrieveLocationByTimeStamp(timeStamp)
    }

    @Query("SELECT MAX(lastUpdateTimeStamp) FROM LocationTable")
    suspend fun retrieveLastUpdateTimeStamp(): Long

    @Query("SELECT * FROM LocationTable WHERE lastUpdateTimeStamp = :timeStamp")
    suspend fun retrieveLocationByTimeStamp(timeStamp: Long): LocationEntity
    //endregion
}