package com.arnab.weatherforecast.repo

import android.location.Location
import android.util.Log
import com.arnab.network.RetrofitClient
import com.arnab.network.WeatherApi
import com.arnab.network.response.ForecastResponse
import com.arnab.network.response.WeatherResponse
import com.arnab.weatherforecast.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherRepository : Repository {

    private val mTAG = "WeatherRepository"

    override fun getCurrentGeometricLocation(): Location {
        TODO("Not yet implemented")
    }

    override fun getSavedGeometricLocations(): List<Location> {
        TODO("Not yet implemented")
    }

    override fun saveGeometricLocation(location: Location) {
        TODO("Not yet implemented")
    }

    override fun deleteGeometricLocation() {
        TODO("Not yet implemented")
    }


    /**
     * **Current Weather Report API** request helper
     *
     * @param location          GeometricLocation(latitude, longitude)
     * @return                  Nullable WeatherResponse
     */
    override fun getCurrentWeatherReportForGeometricLocation(location: Location?): WeatherResponse? {
        val weatherApi = RetrofitClient.retrofitInstance!!.create(WeatherApi::class.java)
        val response: Response<WeatherResponse?> = weatherApi.getCurrentWeatherReportAPI(
            latitude = location?.latitude?.toString() ?: "22.6753717",
            longitude = location?.longitude?.toString() ?: "88.852145",
            units = "metric",
        ).execute()
        return if (response.isSuccessful) {
            Log.i(mTAG, "Status code:${response.code()}")
            response.body()
        } else {
            Log.e(mTAG, "Status code: ${response.code()}")
            Log.e(mTAG, "Error: ${response.message()}")
            null
        }
    }


    /**
     * **Weather Forecast API** request helper
     *
     * @param location          GeometricLocation(latitude, longitude)
     * @return                  Nullable ForecastResponse
     */
    override fun getWeatherForecastForGeometricLocation(location: Location): ForecastResponse? {
        var forecastResponse: ForecastResponse? = null
        val weatherApi = RetrofitClient.retrofitInstance!!.create(WeatherApi::class.java)
        weatherApi.getWeatherForecastAPI(latitude = location.latitude.toString(), longitude = location.longitude.toString())
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                    if (response.isSuccessful) {
                        Log.i(mTAG, "getWeatherForecastForGeometricLocation() Successful Response!!")
                        forecastResponse = response.body()
                    } else {
                        Log.e(mTAG, "getWeatherForecastForGeometricLocation() Status code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    Log.e(mTAG, "getWeatherForecastForGeometricLocation() onFailure: ${t.message}")
                }

            })

        return forecastResponse
    }
}