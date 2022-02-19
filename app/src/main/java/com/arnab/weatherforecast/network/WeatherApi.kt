package com.arnab.weatherforecast.network

import com.arnab.weatherforecast.network.response.ForecastResponse
import com.arnab.weatherforecast.network.response.WeatherResponse
import com.arnab.weatherforecast.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * *Retrofit interface for Weather API call.*
 */
interface WeatherApi {

    /**
     * **Current Weather Report API**
     *
     * @param latitude**(required)**    Geographical coordinates (**latitude**, longitude)
     * @param longitude**(required)**   Geographical coordinates (latitude, **longitude**)
     * @param units**(optional)**       Units of measurement. standard, metric and imperial units are available.If you do not use the units parameter, standard units will be applied by default.API response temperature will be **C°/F°** based on this parameter **metric/imperial**
     * @param apiKey**(optional)**      Your unique API key (you can always find it on your account page under the "API key" tab). It's a required parameter for this API. But as its set in this interface by default. No need to specify explicitly at the implementation time
     * @return                          WeatherResponse. Temperature in **C° or F°** based on **units** parameter.
     */
    @GET("data/2.5/weather?")
    fun getCurrentWeatherReportAPI(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String = "metric",
        @Query("appId") apiKey: String = Constants.WEATHER_API_KEY
    ): Call<WeatherResponse>


    /**
     * **Weather Forecast API**
     *
     * @param latitude**(required)**    Geographical coordinates (**latitude**, longitude)
     * @param longitude**(required)**   Geographical coordinates (latitude, **longitude**)
     * @param units**(optional)**       Units of measurement. standard, metric and imperial units are available.If you do not use the units parameter, standard units will be applied by default.API response temperature will be **C°/F°** based on this parameter **metric/imperial**
     * @param apiKey**(optional)**      Your unique API key (you can always find it on your account page under the "API key" tab). It's a required parameter for this API. But as its set in this interface by default. No need to specify explicitly at the implementation time
     * @return                          WeatherResponse. Temperature in **C° or F°** based on **units** parameter.
     */
    @GET("data/2.5/forecast?")
    fun getWeatherForecastAPI(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String = "metric",
        @Query("appId") apiKey: String = Constants.WEATHER_API_KEY
    ): Call<ForecastResponse>


    /**
     * **Air Pollution API**
     *
     * @param latitude**(required)**    Geographical coordinates (**latitude**, longitude)
     * @param longitude**(required)**   Geographical coordinates (latitude, **longitude**)
     * @param apiKey**(optional)**      Your unique API key (you can always find it on your account page under the "API key" tab). It's a required parameter for this API. But as its set in this interface by default. No need to specify explicitly at the implementation time
     * @return                          AirPollutionResponse.
     */
    @GET("data/2.5/air_pollution?")
    fun getCurrentAirPollutionAPI(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appId") apiKey: String = Constants.WEATHER_API_KEY
    ): Call<Response>


    /**
     * **Air Pollution Forecast API**
     *
     * @param latitude**(required)**    Geographical coordinates (**latitude**, longitude)
     * @param longitude**(required)**   Geographical coordinates (latitude, **longitude**)
     * @param apiKey**(optional)**      Your unique API key (you can always find it on your account page under the "API key" tab). It's a required parameter for this API. But as its set in this interface by default. No need to specify explicitly at the implementation time
     * @return                          AirPollutionResponse.
     */
    @GET("data/2.5/air_pollution/forecast?")
    fun getAirPollutionForecastAPI(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appId") apiKey: String = Constants.WEATHER_API_KEY
    ): Call<Response>


    /**
     * **Air Pollution History API**
     *
     * @param latitude**(required)**    Geographical coordinates (**latitude**, longitude)
     * @param longitude**(required)**   Geographical coordinates (latitude, **longitude**)
     * @param startTimeUTC**(required)** Start time in UTC for range of history data
     * @param endTimeUTC**(required)**  End time in UTC for range of history data
     * @param apiKey**(optional)**      Your unique API key (you can always find it on your account page under the "API key" tab). It's a required parameter for this API. But as its set in this interface by default. No need to specify explicitly at the implementation time
     * @return                          AirPollutionResponse.
     */
    @GET("data/2.5/air_pollution/history?")
    fun getAirPollutionHistoryAPI(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("start") startTimeUTC: Long = 1606488670,
        @Query("end") endTimeUTC: Long = 1606747870,
        @Query("appId") apiKey: String = Constants.WEATHER_API_KEY
    ): Call<Response>


    /**
     * **Get Coordinates by location name**
     *
     * @param cityNameStateCodeCountryCode      CityName, StateCode, CountryCode in comma separated format. *For Ex.(Kolkata,WB,+91)*
     * @param limit                             Limit Number of results in API response
     * @param apiKey**(optional)**              Your unique API key (you can always find it on your account page under the "API key" tab). It's a required parameter for this API. But as its set in this interface by default. No need to specify explicitly at the implementation time
     */
    @GET("geo/1.0/direct?")
    fun getCoordinatesByLocationName(
        @Query("q") cityNameStateCodeCountryCode: String,
        @Query("limit") limit: Int,
        @Query("appId") apiKey: String = Constants.WEATHER_API_KEY
    ): Call<Response>


    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): WeatherApi {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApi::class.java)
        }
    }
}