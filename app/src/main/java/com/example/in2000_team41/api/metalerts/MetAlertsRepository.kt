package com.example.in2000_team41.api.metalerts

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

class MetAlertsRepository {
    private val baseUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/metalerts/1.1?"
    private var alerts = listOf<MetAlertModel>()
    private var metAlertsLive = MutableLiveData<List<MetAlertModel>>()

    // henter MetAlerts-responsen og setter dataen til LiveData listen
    fun apiResponse() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = Fuel.get(baseUrl).awaitString()
                Handler(Looper.getMainLooper()).post {
                    val inputStream: InputStream = response.byteInputStream() // read xml byte for byte
                    alerts = MetAlertsParser().parse(inputStream)
                    notifyLiveData(alerts)
                }
            } catch (exception: FuelError) {
                println("** METALERTS - A network request exception was thrown: ${exception.message}")
            }

        }
    }

    fun notifyLiveData(list: List<MetAlertModel>) {
        metAlertsLive.value = list
    }

    fun metAlertsLiveData() = metAlertsLive as LiveData<List<MetAlertModel>>


}