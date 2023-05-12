package com.arnab.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arnab.database.dao.LocationDao
import com.arnab.database.entity.LocationEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var locationDao: LocationDao
    private lateinit var db: WeatherDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        ).build()
        locationDao = db.getLocationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertLocationAndRetrieve() {
        val mockLocation: LocationEntity = LocationEntity(
            name = "Kolkata",
            latitude = 22.0,
            longitude = 88.0
        )

        runBlocking {
            locationDao.insert(mockLocation)
            val location = locationDao.retrieveLocationByName("Kolkata")
            assertEquals(location, mockLocation)
        }
    }


    @Test
    @Throws(Exception::class)
    fun testInsertAndRetrieveAllLocations() {
        val mockLocation01: LocationEntity = LocationEntity(
            name = "Kolkata",
            latitude = 22.0,
            longitude = 88.0
        )
        val mockLocation02: LocationEntity = LocationEntity(
            name = "Bangalore",
            latitude = 21.0,
            longitude = 86.0
        )
        val mockLocation03: LocationEntity = LocationEntity(
            name = "Hyderabad",
            latitude = 28.0,
            longitude = 80.0
        )

        runBlocking {
            locationDao.insert(mockLocation01)
            locationDao.insert(mockLocation02)
            locationDao.insert(mockLocation03)
            val locationList = locationDao.retrieveAllLocations()
            assertEquals(locationList.size, 3)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndDeleteLocationByName() {
        val mockLocation: LocationEntity = LocationEntity(
            name = "Kolkata",
            latitude = 22.0,
            longitude = 88.0
        )

        runBlocking {
            locationDao.insert(mockLocation)
            val deletedItemCount = locationDao.deleteLocationByName("Kolkata")
            assertEquals(deletedItemCount, 1)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndDeleteAllLocations() {
        val mockLocation01: LocationEntity = LocationEntity(
            name = "Kolkata",
            latitude = 22.0,
            longitude = 88.0
        )
        val mockLocation02: LocationEntity = LocationEntity(
            name = "Bangalore",
            latitude = 21.0,
            longitude = 86.0
        )
        val mockLocation03: LocationEntity = LocationEntity(
            name = "Hyderabad",
            latitude = 28.0,
            longitude = 80.0
        )

        runBlocking {
            locationDao.insert(mockLocation01)
            locationDao.insert(mockLocation02)
            locationDao.insert(mockLocation03)
            val deletedItemCount = locationDao.deleteAllLocations()
            assertEquals(deletedItemCount, 3)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testRetrieveLastLocation() {
        runBlocking {

            val mockLocation01: LocationEntity = LocationEntity(
                name = "Kolkata",
                latitude = 22.0,
                longitude = 88.0
            )
            delay(100)

            val mockLocation02: LocationEntity = LocationEntity(
                name = "Bangalore",
                latitude = 21.0,
                longitude = 86.0
            )
            delay(100)

            val mockLocation03: LocationEntity = LocationEntity(
                name = "Hyderabad",
                latitude = 28.0,
                longitude = 80.0
            )

            locationDao.insert(mockLocation01)
            locationDao.insert(mockLocation02)
            locationDao.insert(mockLocation03)
            val location = locationDao.retrieveLastLocation()
            assertEquals(location, mockLocation03)
        }
    }
}