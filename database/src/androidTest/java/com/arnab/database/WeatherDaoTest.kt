package com.arnab.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arnab.database.dao.WeatherDao
import com.arnab.database.entity.WeatherReportEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {
    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        ).build()
        weatherDao = db.getWeatherDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndRetrieveWeatherReport() {
        val mockWeatherReport: WeatherReportEntity = WeatherReportEntity(
            location_id = 1255211,
            location_name = "Tāki",
            location_timezone = 19800,
            visibility = 10000,
            dt = 1683985824,
            latitude = 22.6516,
            longitude = 88.8623,
            main_temperature = 33.35,
            main_temperature_feels_like = 40.35,
            main_temperature_min = 29.97,
            main_temperature_max = 33.35,
            main_pressure = 1004,
            main_humidity = 62,
            main_sea_level = 1004,
            main_grnd_level = 1003,
            weather_id = 804,
            weather_main = "Clouds",
            weather_description = "overcast clouds",
            weather_icon = "04n",
            wind_speed = 7.17,
            wind_degree = 128,
            wind_gust = 10.53,
            sys_id = 2032109,
            sys_type = 2,
            sys_country_code = "IN",
            sys_sunrise = 1683933930,
            sys_sunset = 1683981385,
            lastUpdateTimeStamp = System.currentTimeMillis()
        )

        runBlocking {
            weatherDao.insert(mockWeatherReport)
            val weatherReport = weatherDao.retrieveWeatherReportByLocationName("Tāki")
            assertEquals(weatherReport, mockWeatherReport)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndDeleteWeatherReport() {
        val mockWeatherReport: WeatherReportEntity = WeatherReportEntity(
            location_id = 1255211,
            location_name = "Tāki",
            location_timezone = 19800,
            visibility = 10000,
            dt = 1683985824,
            latitude = 22.6516,
            longitude = 88.8623,
            main_temperature = 33.35,
            main_temperature_feels_like = 40.35,
            main_temperature_min = 29.97,
            main_temperature_max = 33.35,
            main_pressure = 1004,
            main_humidity = 62,
            main_sea_level = 1004,
            main_grnd_level = 1003,
            weather_id = 804,
            weather_main = "Clouds",
            weather_description = "overcast clouds",
            weather_icon = "04n",
            wind_speed = 7.17,
            wind_degree = 128,
            wind_gust = 10.53,
            sys_id = 2032109,
            sys_type = 2,
            sys_country_code = "IN",
            sys_sunrise = 1683933930,
            sys_sunset = 1683981385,
            lastUpdateTimeStamp = System.currentTimeMillis()
        )

        runBlocking {
            weatherDao.insert(mockWeatherReport)
            val numberOfWeatherReportDeleted = weatherDao.deleteWeatherReportByLocationName("Tāki")
            assertEquals(numberOfWeatherReportDeleted, 1)
        }
    }


    @Test
    @Throws(Exception::class)
    fun testInsertAndDeleteAllWeatherReport() {
        val mockWeatherReport1: WeatherReportEntity = WeatherReportEntity(
            location_id = 1255211,
            location_name = "Tāki",
            location_timezone = 19800,
            visibility = 10000,
            dt = 1683985824,
            latitude = 22.6516,
            longitude = 88.8623,
            main_temperature = 33.35,
            main_temperature_feels_like = 40.35,
            main_temperature_min = 29.97,
            main_temperature_max = 33.35,
            main_pressure = 1004,
            main_humidity = 62,
            main_sea_level = 1004,
            main_grnd_level = 1003,
            weather_id = 804,
            weather_main = "Clouds",
            weather_description = "overcast clouds",
            weather_icon = "04n",
            wind_speed = 7.17,
            wind_degree = 128,
            wind_gust = 10.53,
            sys_id = 2032109,
            sys_type = 2,
            sys_country_code = "IN",
            sys_sunrise = 1683933930,
            sys_sunset = 1683981385,
            lastUpdateTimeStamp = System.currentTimeMillis()
        )

        val mockWeatherReport2: WeatherReportEntity = WeatherReportEntity(
            location_id = 1255211,
            location_name = "Tāki",
            location_timezone = 19800,
            visibility = 10000,
            dt = 1683985824,
            latitude = 22.6516,
            longitude = 88.8623,
            main_temperature = 33.35,
            main_temperature_feels_like = 40.35,
            main_temperature_min = 29.97,
            main_temperature_max = 33.35,
            main_pressure = 1004,
            main_humidity = 62,
            main_sea_level = 1004,
            main_grnd_level = 1003,
            weather_id = 804,
            weather_main = "Clouds",
            weather_description = "overcast clouds",
            weather_icon = "04n",
            wind_speed = 7.17,
            wind_degree = 128,
            wind_gust = 10.53,
            sys_id = 2032109,
            sys_type = 2,
            sys_country_code = "IN",
            sys_sunrise = 1683933930,
            sys_sunset = 1683981385,
            lastUpdateTimeStamp = System.currentTimeMillis()
        )

        val mockWeatherReport3: WeatherReportEntity = WeatherReportEntity(
            location_id = 1255211,
            location_name = "Kolkata",
            location_timezone = 19800,
            visibility = 10000,
            dt = 1683985824,
            latitude = 22.0,
            longitude = 88.0,
            main_temperature = 33.35,
            main_temperature_feels_like = 40.35,
            main_temperature_min = 29.97,
            main_temperature_max = 33.35,
            main_pressure = 1004,
            main_humidity = 62,
            main_sea_level = 1004,
            main_grnd_level = 1003,
            weather_id = 804,
            weather_main = "Clouds",
            weather_description = "overcast clouds",
            weather_icon = "04n",
            wind_speed = 7.17,
            wind_degree = 128,
            wind_gust = 10.53,
            sys_id = 2032109,
            sys_type = 2,
            sys_country_code = "IN",
            sys_sunrise = 1683933930,
            sys_sunset = 1683981385,
            lastUpdateTimeStamp = System.currentTimeMillis()
        )

        runBlocking {
            weatherDao.insert(mockWeatherReport1)
            weatherDao.insert(mockWeatherReport2)
            weatherDao.insert(mockWeatherReport3)
            val numberOfWeatherReportDeleted = weatherDao.deleteAllSavedWeatherReports()
            assertEquals(numberOfWeatherReportDeleted, 2)
        }
    }
}