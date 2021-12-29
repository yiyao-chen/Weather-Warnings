package com.example.in2000_team41.ui.map

import androidx.lifecycle.ViewModel
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.api.metalerts.AlertRepository
import com.example.in2000_team41.api.metalerts.MetAlertModel
import com.example.in2000_team41.api.metalerts.MetAlertsRepository
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {

    var listMetAlertModel = listOf<MetAlertModel>()
    var listAlertModel: MutableList<AlertModel> = mutableListOf()
    var mAlertRepo: AlertRepository? = null
    private var mMetAlertsRepo: MetAlertsRepository? = null

    // MetAlerts API
    fun setMetAlertsApiData() {
        if (mMetAlertsRepo == null){        // trenger ikke hente data flere ganger, unødvendig bruk av ressurser
            mMetAlertsRepo = MetAlertsRepository()
            mMetAlertsRepo?.apiResponse()
        }
    }

    fun setFullAlertsApiData(list: List<MetAlertModel>, userPosition: LatLng){
        if (mAlertRepo == null) {
            mAlertRepo = AlertRepository()
            mAlertRepo?.apiResponseFull(list, userPosition)
        }
    }

    fun fullAlertsLiveData() = mAlertRepo?.fullAlertListLiveData()

    // Met Alerts Live Data
    fun metAlertsLiveData() = mMetAlertsRepo?.metAlertsLiveData()

    fun updateMetAlertList(newList: List<MetAlertModel>){
        listMetAlertModel = newList
    }

/*
    // Alert-data fra linker i MetAlerts
    fun setAlertApiData(url: String) {
        mAlertRepo = AlertRepository()
        mAlertRepo?.apiResponse(url)
    }

 */


    // Setter referansen  i repo til riktig objekt
    fun setAlertDataMap(position: Int) {
        mAlertRepo?.setAlertMap(position)
    }

    // Alert Live Data
    fun alertLiveData() = mAlertRepo?.alertLiveData()

    fun updateAlertList(newList: MutableList<AlertModel>) {
        listAlertModel = newList
    }

    /*fun updateAreaList(area: Area){
        if (area !in listOfAreas){
            listOfAreas.add(area)
        }
    }*/
}
