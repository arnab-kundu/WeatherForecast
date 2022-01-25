package com.arnab.weatherforecast.network

import com.arnab.weatherforecast.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var retrofit: Retrofit? = null

    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpClient.readTimeout(3000, TimeUnit.MILLISECONDS)
                okHttpClient.connectTimeout(3000, TimeUnit.MILLISECONDS)
                okHttpClient.addInterceptor(interceptor)
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}