package com.arnab.database

import androidx.room.RoomDatabase
import com.arnab.database.dao.LocationDao
import com.arnab.database.entity.LocationEntity


@androidx.room.Database(
    version = 1,
    entities = [LocationEntity::class],
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getLocationDao(): LocationDao


}