package com.arnab.weatherforecast

import android.annotation.SuppressLint
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.arnab.network.RetrofitClient
import com.arnab.network.WeatherApi
import com.arnab.network.response.ForecastResponse
import com.arnab.network.response.WeatherResponse
import com.arnab.weatherforecast.constants.ImageUrls
import com.arnab.weatherforecast.constants.WeatherMain
import com.arnab.weatherforecast.repo.WeatherRepository
import com.arnab.weatherforecast.ui.theme.WeatherForecastTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.math.roundToInt


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    //val mTemperature = MutableStateFlow(0)
    // or
    val mTemperature = mutableStateOf(0)
    val mMaxTemperature = mutableStateOf(0)
    val mMinTemperature = mutableStateOf(0)
    val mVisibilityDistance = mutableStateOf(0)
    val mHumidity = mutableStateOf(0)
    val mLocationName = mutableStateOf("")
    val mWeatherMain = mutableStateOf(WeatherMain.CLEAR.name)


    //Define a request code to send to Google Play services
    private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherForecastTheme {
                // A surface container using the 'background' color from the theme
                getBackgroundImageFromLocal()
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


        mGoogleApiClient =
            GoogleApiClient.Builder(this) // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this) //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build()

        // Create the LocationRequest object

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10 * 1000) // 10 seconds, in milliseconds
            .setFastestInterval(1 * 1000) // 1 second, in milliseconds


    }

    override fun onResume() {
        super.onResume()
        //Now lets connect to the API
        mGoogleApiClient?.connect()
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause()")

        //Disconnect from API onPause()
        if (mGoogleApiClient?.isConnected() == true) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient?.disconnect()
        }


    }

    @Deprecated("unused")
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

                }
            })
    }

    @Deprecated("unused")
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

    @Deprecated("unused")
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

    /**
     * API call
     */
    private fun backgroundThreadWork(location: Location?) {
        val repo = WeatherRepository()
        CoroutineScope(context = Dispatchers.Main).launch {
            val deferred: Deferred<WeatherResponse?> = CoroutineScope(context = Dispatchers.IO).async {
                delay(5000)
                repo.getCurrentWeatherReportForGeometricLocation(location)// TODO provide location(lat,long)
            }
            val responseBody: WeatherResponse? = deferred.await()
            if (responseBody != null) {
                Log.i(TAG, "Response: ${responseBody.toString()}")
                mTemperature.value = responseBody.main.temp.roundToInt()
                mMaxTemperature.value = responseBody.main.temp_max.roundToInt()
                mMinTemperature.value = responseBody.main.temp_min.roundToInt()
                mHumidity.value = responseBody.main.humidity
                mVisibilityDistance.value = responseBody.visibility / 1000
                mLocationName.value = responseBody.name
                mWeatherMain.value = responseBody.weather[0].main
            }
        }

    }


    @Composable
    fun getPlaceName(placeName: String) {
        val locationName by mLocationName
        Text(
            text = locationName,
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
    fun getBackgroundImageFromLocal() {
        val weatherMain by mWeatherMain
        mWeatherMain
        var drawableId = R.drawable.clear_sky_image
        when (weatherMain) {
            WeatherMain.CLEAR.name -> drawableId = R.drawable.clear_night
            WeatherMain.CLEAR.name -> drawableId = R.drawable.clear_sky_image
            WeatherMain.CLOUDS.name -> drawableId = R.drawable.cloudy
            WeatherMain.RAIN.name -> drawableId = R.drawable.after_shower
            WeatherMain.RAIN.name -> drawableId = R.drawable.light_rain
            else -> Exception("No matching climate found")
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

    // TODO Add the Missing Permission CHECK for Location
    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (location == null) {
            Log.e(TAG, "onConnected: Location: $location")
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.latitude
            currentLongitude = location.longitude
            Toast.makeText(this, "Lat: $currentLatitude, Long: $currentLongitude", Toast.LENGTH_LONG).show();
            Log.i(TAG, "onConnected: Lat: $currentLatitude, Long: $currentLongitude")
            backgroundThreadWork(location = location)
        }
    }

    override fun onConnectionSuspended(i: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

        /** Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST)
                /**
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (e: SendIntentException) {
                // Log the error
                e.printStackTrace()
            }
        } else {
            /**
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.errorCode)
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            currentLatitude = location.getLatitude()
            currentLongitude = location.getLongitude()
        }
        Toast.makeText(this, "Lat: $currentLatitude, Long: $currentLongitude", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onLocationChanged: Lat: $currentLatitude, Long: $currentLongitude")
        //backgroundThreadWork(location = location)
    }
}