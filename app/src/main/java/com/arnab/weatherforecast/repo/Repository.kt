package com.arnab.weatherforecast.repo

import android.location.Location
import com.arnab.network.response.ForecastResponse
import com.arnab.network.response.WeatherResponse

interface Repository {

    fun getCurrentGeometricLocation(): Location

    fun getSavedGeometricLocations(): List<Location>

    fun saveGeometricLocation(location: Location)

    fun deleteGeometricLocation()

    fun getCurrentWeatherReportForGeometricLocation(location: Location?): WeatherResponse?

    fun getWeatherForecastForGeometricLocation(location: Location): ForecastResponse?
}