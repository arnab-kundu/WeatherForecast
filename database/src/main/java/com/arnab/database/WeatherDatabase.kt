package com.arnab.database

import androidx.room.RoomDatabase
import com.arnab.database.dao.LocationDao
import com.arnab.database.dao.WeatherDao
import com.arnab.database.entity.LocationEntity
import com.arnab.database.entity.WeatherReportEntity


@androidx.room.Database(
    version = 1,
    entities = [LocationEntity::class, WeatherReportEntity::class],
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getLocationDao(): LocationDao
    abstract fun getWeatherDao(): WeatherDao
}