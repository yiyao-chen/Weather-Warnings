package com.example.in2000_team41.api.avalanche

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

class AvalancheRepository {
    private val gson = Gson()
    private val baseUrl = "https://api01.nve.no/hydrology/forecast/avalanche/v6.0.0/api/"
    private val queryUrl = "RegionSummary/Simple/1/"
    private var fullList = listOf<AvalancheModel>()
    private val avalancheResponse = MutableLiveData<List<AvalancheModel>>()

    fun apiResponse() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = gson.fromJson(Fuel.get(baseUrl+queryUrl).awaitString(), Array<AvalancheModel>::class.java).toList()
                Handler(Looper.getMainLooper()).post {
                    fullList = response
                    notifyLiveData(fullList)
                }
            } catch (exception: FuelError) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }

    fun getFullList() = fullList

    fun notifyLiveData(list: List<AvalancheModel>){
        avalancheResponse.value = list
    }

    fun avalancheLiveData() = avalancheResponse as LiveData<List<AvalancheModel>>

    // ----------

}