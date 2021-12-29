package com.example.in2000_team41.api.avalanche

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class AvalancheSimpleRepository {
    private val gson = Gson()
    private val baseUrl = "https://api01.nve.no/hydrology/forecast/avalanche/v6.0.0/api/AvalancheWarningByCoordinates/Simple/"
    private var fullList = listOf<AvalancheWarning>()
    private val avalancheResponse = MutableLiveData<List<AvalancheWarning>>()

    fun apiResponse(latitude: String, longitude: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var response: List<AvalancheWarning>? = null
                try {
                    response = gson.fromJson(Fuel.get("$baseUrl/$latitude/$longitude").awaitString(), Array<AvalancheWarning>::class.java).toList()
                } catch (exception: Exception){
                    println("Error: ${exception.message}")
                }
                Handler(Looper.getMainLooper()).post {
                    if (response != null){
                        fullList = response
                        notifyLiveData(fullList)
                    }
                }
            } catch (exception: FuelError) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }

    fun getFullListData() = fullList

    private fun notifyLiveData(list: List<AvalancheWarning>){
        avalancheResponse.value = list
    }

    fun avalancheLiveData() = avalancheResponse as LiveData<List<AvalancheWarning>>

    // ----------

}