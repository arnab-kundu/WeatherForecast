package com.arnab.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(
    tableName = "WeatherTable",
    indices = [Index(value = ["latitude", "longitude"], unique = true)]
)
data class WeatherReportEntity(

    val location_id: Int?,
    val location_name: String?,
    val location_timezone: Int,
    val visibility: Int,
    val dt: Int,

    val latitude: Double?,
    val longitude: Double?,

    val main_temperature: Double,
    val main_temperature_feels_like: Double,
    val main_temperature_min: Double,
    val main_temperature_max: Double,
    val main_pressure: Int,
    val main_humidity: Int,
    val main_sea_level: Int,
    val main_grnd_level: Int,

    val weather_id: Int,
    val weather_main: String,
    val weather_description: String,
    val weather_icon: String,

    val wind_speed: Double,
    val wind_degree: Int,
    val wind_gust: Double,

    val sys_id: Int,
    val sys_type: Int,
    val sys_country_code: String,
    val sys_sunrise: Int,
    val sys_sunset: Int,

    var lastUpdateTimeStamp: Long = System.currentTimeMillis()
) : Parcelable{
    @IgnoredOnParcel
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
}
