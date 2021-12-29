package com.example.in2000_team41.api.forestfire

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
import java.util.*

class ForestfireRepository {
    private val gson = Gson()
    private val forestfireUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/forestfireindex/1.1/.json"
    private var fullList = listOf<ForestfireModel>()
    private val forestfireResponse = MutableLiveData<List<ForestfireModel>>()


    // henter Forestfire-responsen og setter dataen til LiveData listen
    fun apiResponse() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = gson.fromJson(Fuel.get(forestfireUrl).awaitString(), Array<ForestfireModel>::class.java).toList()
                Handler(Looper.getMainLooper()).post {
                    fullList = response
                    notifyLiveData(fullList)
                }
            } catch (exception: FuelError) {
                println("**FORESTFIRE - A network request exception was thrown: ${exception.message}")
            }
        }
    }



    fun getFullList() = fullList

    fun notifyLiveData(list: List<ForestfireModel>){
        forestfireResponse.value = list
    }

    fun forestfireLiveData() = forestfireResponse as LiveData<List<ForestfireModel>>

    // ----------

}