package com.example.in2000_team41.api.locationforecast

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LocationForecastRepository {
    private val gson = Gson()
    private val baseUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/complete?lat="
    private val url2 = "&lon="
    private var data: LocationForecastModel? = null
    private val locationforecastResponse = MutableLiveData<LocationForecastModel>()


    // henter LocationForecast-responsen og setter dataen til LiveData listen
    fun apiResponse(lat: String, long: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = gson.fromJson(Fuel.get(baseUrl+lat+url2+long).awaitString(), LocationForecastModel::class.java)
                Handler(Looper.getMainLooper()).post {
                    data = response
                    notifyLiveData()
                }
            } catch (exception: FuelError) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }


    private fun notifyLiveData() {
        locationforecastResponse.value = data!!
    }

    fun locationForecastData() = locationforecastResponse as LiveData<LocationForecastModel>

    // ----------




}