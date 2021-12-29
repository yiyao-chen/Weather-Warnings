package com.example.in2000_team41.api.metalerts

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

class AlertRepository {
    private lateinit var alert: AlertModel
    private var alertLive = MutableLiveData<AlertModel>()

    private var alertList = mutableListOf<AlertModel>()
    private var alertListLive = MutableLiveData<MutableList<AlertModel>>()
    private lateinit var position: LatLng


    // henter ALLE Alerts
    fun apiResponseFull(metAlertsResponse: List<MetAlertModel>, userPosition: LatLng) {
        position = userPosition
        for (metalert in metAlertsResponse){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = Fuel.get(metalert.link!!).awaitString()
                    Handler(Looper.getMainLooper()).post {
                        val inputStream: InputStream = response.byteInputStream() // read xml byte for byte
                        val partAlert = AlertParser().parse(inputStream)
                        // Regner ut avstanden mellom Oslo og farevarselet
                        val closestCoordinates = findClosestCoordinates(partAlert.info?.area?.polygon!!)
                        val distance = calculateDistance(closestCoordinates)
                        partAlert.distance = distance

                        if (partAlert !in alertList) {
                            alertList.add(partAlert)
                            notifyLiveList(alertList)
                        }
                    }
                } catch (exception: FuelError) {
                    println("** ALERT -> A network request exception was thrown: ${exception.message}")
                }
            }
        }
    }

    fun notifyLiveList(alertList: MutableList<AlertModel>){
        alertListLive.value = alertList
    }
    fun fullAlertListLiveData() = alertListLive as LiveData<MutableList<AlertModel>>


    // Referanse fra onClick() på listen
    fun setAlert(alertClicked: AlertModel) {
        alert = alertClicked
        notifyLiveData(alert)
    }
    // Referansen fra onClick() på kart
    fun setAlertMap(position: Int) {
        alert = alertList[position]
        notifyLiveData(alert)
    }


    // henter 1 Alert-respons og setter dataen til LiveData listen
    fun apiResponse(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = Fuel.get(url).awaitString()
                Handler(Looper.getMainLooper()).post {
                    val inputStream: InputStream = response.byteInputStream() // read xml byte for byte
                    alert = AlertParser().parse(inputStream)
                    // Regner ut avstanden mellom Oslo og farevarselet
                    val closestCoordinates = findClosestCoordinates(alert.info?.area?.polygon!!)
                    val distance = calculateDistance(closestCoordinates)
                    alert.distance = distance
                    notifyLiveData(alert)
                    if (alert !in alertList) {
                        alertList.add(alert)
                        notifyLiveList(alertList)
                    }
                }
            } catch (exception: FuelError) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }


    private fun notifyLiveData(alert: AlertModel) {
        alertLive.value = alert
    }

    fun alertLiveData() = alertLive as LiveData<AlertModel>





    fun calculateDistance(closestCoordinates: String) : Double{
        val currentItemCoordinates = LatLng(closestCoordinates.split(',')[0].toDouble(), closestCoordinates.split(',')[1].toDouble())
        val distance = SphericalUtil.computeDistanceBetween(position, currentItemCoordinates)
        return distance/1000
    }


    // ___________________________
    // Sjekker hvilke coordinater som er nærmest brukerens posisjon fra polygonet
    fun findClosestCoordinates(polygonList: String): String {
        val positionLatitude = position.latitude
        val positionLongitude = position.longitude
        var closestLatitude = 0.0
        var closestLongitude = 0.0
        val firstSplit = polygonList.split(" ")
        for (set in firstSplit) {
            val result0 = set.filter { it.isDigit() || it == '.' || it == ','}
            val coordinatePair = result0.split(",")
            if (coordinatePair[0].isNotEmpty() && coordinatePair[1].isNotEmpty()) {
                val latitude = coordinatePair[0].toDouble()
                val longitude = coordinatePair[1].toDouble()
                // Sjekker nærmeste latitude
                if (closestLatitude == 0.0) {
                    closestLatitude = latitude
                } else {
                    if ((latitude > positionLatitude) && (latitude < closestLatitude)) closestLatitude = latitude
                    if ((latitude < positionLatitude) && (latitude > closestLatitude)) closestLatitude = latitude
                }
                // Sjekker nærmeste longitude
                if (closestLongitude == 0.0) {
                    closestLongitude = longitude
                } else {
                    if ((longitude > positionLongitude) && (longitude < closestLongitude)) closestLongitude = longitude
                    if ((longitude < positionLongitude) && (longitude > closestLongitude)) closestLongitude = longitude
                }
            }
        }
        return "$closestLatitude,$closestLongitude"
    }
}