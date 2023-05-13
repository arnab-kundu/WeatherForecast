package com.arnab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnab.database.entity.WeatherReportEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherReportEntity: WeatherReportEntity)

    @Query("SELECT * FROM WeatherTable WHERE location_name LIKE :locationName")
    suspend fun retrieveWeatherReportByLocationName(locationName: String): WeatherReportEntity

    @Query("DELETE FROM WeatherTable WHERE location_name LIKE :locationName")
    suspend fun deleteWeatherReportByLocationName(locationName: String): Int

    @Query("DELETE FROM WeatherTable WHERE 1 = 1")
    suspend fun deleteAllSavedWeatherReports(): Int
}