package com.arnab.weatherforecast

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.arnab.weatherforecast.constants.ImageUrls
import com.arnab.weatherforecast.network.RetrofitClient
import com.arnab.weatherforecast.network.WeatherApi
import com.arnab.weatherforecast.network.response.ForecastResponse
import com.arnab.weatherforecast.network.response.WeatherResponse
import com.arnab.weatherforecast.repo.WeatherRepository
import com.arnab.weatherforecast.ui.theme.WeatherForecastTheme
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    val mTemperature = mutableStateOf(0)
    val mMaxTemperature = mutableStateOf(0)
    val mMinTemperature = mutableStateOf(0)
    val mVisibilityDistance = mutableStateOf(0)
    val mHumidity = mutableStateOf(0)
    // or
    //val mTemperature = MutableStateFlow(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundThreadWork()
        setContent {
            WeatherForecastTheme {
                // A surface container using the 'background' color from the theme
                getBackgroundImage()
                Surface(color = Color.Transparent, modifier = Modifier
                    .padding(16.dp)
                    .clickable { }) {
                    Column() {
                        Row(Modifier.weight(1.0F)) {
                            Column(Modifier.weight(1.0F)) {
                                getPlaceName(placeName = "KOLKATA")
                                getTemperature(0, "C")
                                getMaxMinTemperature(maxTemp = 10, minTemp = 25, tempUnit = "C")
                            }
                            Column() {
                                getWeatherDescription(description = "Cloudy")
                            }
                        }
                        val verticalGradientBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x4D000000),
                                Color(0x4D000000),
                                Color(0x4D000000)
                            )
                        )

                        Card(
                            border = BorderStroke(2.dp, Color.Gray)
                        ) {
                            Row(
                                Modifier
                                    .background(brush = verticalGradientBrush, shape = RoundedCornerShape(10.dp))
                                    .padding(5.dp)
                            ) {
                                Column(modifier = Modifier.weight(0.33F), horizontalAlignment = Alignment.CenterHorizontally) {
                                    getHumidity(78)
                                    Text(text = "Humidity", color = Color.White, fontSize = 12.sp)
                                }
                                Column(modifier = Modifier.weight(0.33F), horizontalAlignment = Alignment.CenterHorizontally) {
                                    getVisibility(5)
                                    Text(text = "Visibility", color = Color.White, fontSize = 12.sp)
                                }
                                Column(modifier = Modifier.weight(0.33F), horizontalAlignment = Alignment.CenterHorizontally) {
                                    getUVIndex("Low")
                                    Text(text = "UVIndex", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun networkCall() {
        val weatherApi = RetrofitClient.retrofitInstance!!.create(WeatherApi::class.java)
        weatherApi.getCurrentWeatherReportAPI("22.6753717", "88.852145")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody == null) {
                            Log.e(TAG, "onResponse: Error")
                        } else {
                            Log.i(TAG, "Response: ${responseBody.toString()}")
                            mTemperature.value = responseBody.main.temp.roundToInt()
                            mMaxTemperature.value = responseBody.main.temp_max.roundToInt()
                            mMinTemperature.value = responseBody.main.temp_min.roundToInt()
                            mHumidity.value = responseBody.main.humidity
                            mVisibilityDistance.value = responseBody.visibility / 1000
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    //TODO("Not yet implemented")
                }
            })
    }

    fun networkRepo() {
        val repo = WeatherRepository()
        val responseBody: WeatherResponse? = repo.getCurrentWeatherReportForGeometricLocation(null)
        if (responseBody != null) {
            Log.i(TAG, "Response: ${responseBody.toString()}")
            mTemperature.value = responseBody.main.temp.roundToInt()
            mMaxTemperature.value = responseBody.main.temp_max.roundToInt()
            mMinTemperature.value = responseBody.main.temp_min.roundToInt()
            mHumidity.value = responseBody.main.humidity
            mVisibilityDistance.value = responseBody.visibility / 1000
        }
    }

    private fun networkCallWeatherForecastAPI() {
        val weatherApi = RetrofitClient.retrofitInstance!!.create(WeatherApi::class.java)
        weatherApi.getWeatherForecastAPI("22.65", "88.85")
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                    //TODO("Not yet implemented")
                    if (response.isSuccessful) {
                        Log.i(TAG, "onResponse: ")
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    //TODO("Not yet implemented")
                }

            })
    }

    private fun backgroundThreadWork() {
        val repo = WeatherRepository()
        CoroutineScope(context = Dispatchers.Main).launch {
            val deferred: Deferred<WeatherResponse?> = CoroutineScope(context = Dispatchers.IO).async {
                delay(5000)
                repo.getCurrentWeatherReportForGeometricLocation(null)// TODO provide location(lat,long)
            }
            val responseBody: WeatherResponse? = deferred.await()
            if (responseBody != null) {
                Log.i(TAG, "Response: ${responseBody.toString()}")
                mTemperature.value = responseBody.main.temp.roundToInt()
                mMaxTemperature.value = responseBody.main.temp_max.roundToInt()
                mMinTemperature.value = responseBody.main.temp_min.roundToInt()
                mHumidity.value = responseBody.main.humidity
                mVisibilityDistance.value = responseBody.visibility / 1000
            }
        }

    }


    @Composable
    fun getPlaceName(placeName: String) {
        Text(
            text = placeName,
            modifier = Modifier,
            color = Color.White,
            fontSize = 20.sp,
            fontStyle = null,
            fontWeight = FontWeight(1),
            fontFamily = null,
            letterSpacing = TextUnit.Unspecified,
            textDecoration = null,
            textAlign = null,
            lineHeight = TextUnit.Unspecified,
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = 1,
            onTextLayout = {},
            style = LocalTextStyle.current
        )
    }


    @Composable
    fun getTemperature(temp: Int, tempUnit: String) {
        val temperature by mTemperature
        //val temperature by mTemperature.collectAsState()
        Text(
            text = "$temperature°$tempUnit",
            modifier = Modifier,
            color = Color.White,
            fontSize = 90.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight(1),
            fontFamily = null,
            letterSpacing = TextUnit.Unspecified,
            textDecoration = null,
            textAlign = null,
            lineHeight = TextUnit.Unspecified,
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = 1,
            onTextLayout = {},
            style = LocalTextStyle.current
        )
    }

    @Composable
    fun getMaxMinTemperature(maxTemp: Int, minTemp: Int, tempUnit: String) {
        val maxTemperature by mMaxTemperature
        val minTemperature by mMinTemperature
        Text(
            text = "$minTemperature°$tempUnit/$maxTemperature°$tempUnit",
            modifier = Modifier,
            color = Color.White,
            fontSize = 16.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight(1),
            fontFamily = null,
            letterSpacing = TextUnit.Unspecified,
            textDecoration = null,
            textAlign = null,
            lineHeight = TextUnit.Unspecified,
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = 1,
            onTextLayout = {},
            style = LocalTextStyle.current
        )
    }

    @Composable
    fun getWeatherDescription(description: String) {
        Text(
            text = description,
            modifier = Modifier
                .padding(2.dp)
                .rotate(-90f),
            maxLines = 1,
            color = Color.White,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
        )

    }

    @Composable
    fun getBackgroundImage() {
        WeatherForecastTheme {
            Image(
                painter = rememberImagePainter(data = ImageUrls.CLEAR_SKY_IMAGE /*builder = { transformations(CircleCropTransformation()) }*/),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        }
    }

    @Composable
    fun getBackgroundImageFromLocal(weatherDescription: String) {

        var drawableId = R.drawable.clear_sky_image
        if (weatherDescription == "scattered clouds") {
            drawableId = R.drawable.scattered_clouds
        }
        WeatherForecastTheme {
            Image(
                painter = painterResource(drawableId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        }
    }

    @Composable
    fun getHumidity(humidity: Int) {
        val humidity by mHumidity
        Text(text = "$humidity%", color = Color.White, fontSize = 16.sp)
    }

    @Composable
    fun getVisibility(distance: Int) {
        val distance by mVisibilityDistance
        Text(text = "$distance km", color = Color.White, fontSize = 16.sp)
    }

    @Composable
    fun getUVIndex(uvIndex: String) {
        Text(text = uvIndex, color = Color.White, fontSize = 16.sp)
    }

    @Preview
    @Composable
    fun getLayoutPreview() {
        Image(
            painter = painterResource(R.drawable.clear_sky_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )
        Surface(color = Color.Transparent, modifier = Modifier
            .padding(16.dp)
            .clickable { }) {
            Column() {
                Row(Modifier.weight(1.0F)) {
                    Column(Modifier.weight(1.0F)) {
                        getPlaceName(placeName = "KOLKATA")
                        getTemperature(9, "C")
                        getMaxMinTemperature(maxTemp = 10, minTemp = 25, tempUnit = "C")
                    }
                    Column() {
                        getWeatherDescription(description = "Cloudy")
                    }
                }
                val verticalGradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x4D000000),
                        Color(0x4D000000),
                        Color(0x4D000000)
                    )
                )

                Card(
                    border = BorderStroke(2.dp, Color.Gray)
                ) {
                    Row(
                        Modifier
                            .background(brush = verticalGradientBrush, shape = RoundedCornerShape(10.dp))
                            .padding(5.dp)
                    ) {
                        Column(modifier = Modifier.weight(0.33F), horizontalAlignment = Alignment.CenterHorizontally) {
                            getHumidity(78)
                            Text(text = "Humidity", color = Color.White, fontSize = 12.sp)
                        }
                        Column(modifier = Modifier.weight(0.33F), horizontalAlignment = Alignment.CenterHorizontally) {
                            getVisibility(5)
                            Text(text = "Visibility", color = Color.White, fontSize = 12.sp)
                        }
                        Column(modifier = Modifier.weight(0.33F), horizontalAlignment = Alignment.CenterHorizontally) {
                            getUVIndex("Low")
                            Text(text = "UVIndex", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}