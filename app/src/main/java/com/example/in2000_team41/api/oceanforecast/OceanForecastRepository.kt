package com.example.in2000_team41.api.oceanforecast

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

class OceanForecastRepository {
    private val gson = Gson()
    private val baseUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/oceanforecast/2.0/complete?lat="
    private val url2 = "&lon="
    private var data: OceanForecastModel? = null
    private val oceanforecastResponse = MutableLiveData<OceanForecastModel>()


    // henter LocationForecast-responsen og setter dataen til LiveData listen
    fun apiResponse(lat: String, long: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = gson.fromJson(Fuel.get(baseUrl+lat+url2+long).awaitString(), OceanForecastModel::class.java)
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
        oceanforecastResponse.value = data!!
    }

    fun oceanForecastLiveData() = oceanforecastResponse as LiveData<OceanForecastModel>

    // ----------




}