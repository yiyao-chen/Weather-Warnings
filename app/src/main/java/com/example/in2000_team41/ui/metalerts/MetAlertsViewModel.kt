package com.example.in2000_team41.ui.metalerts

import androidx.lifecycle.ViewModel
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.api.metalerts.AlertRepository
import com.example.in2000_team41.api.metalerts.MetAlertModel
import com.example.in2000_team41.api.metalerts.MetAlertsRepository
import com.google.android.gms.maps.model.LatLng
import java.util.*

class MetAlertsViewModel : ViewModel() {

    // MetAlert variables
    private var mMetAlertsRepo: MetAlertsRepository? = null
    private var mAlertRepo: AlertRepository? = null
    private var metFullList = listOf<AlertModel>()
    private var metLastList = mutableListOf<AlertModel>()
    var infoClicked = false
    var dataCached = false

    // MetAlerts relevant
    fun setMetAlertsApiData() {
        if (mMetAlertsRepo == null){        // trenger ikke hente data flere ganger, unødvendig bruk av ressurser
            mMetAlertsRepo = MetAlertsRepository()
            mMetAlertsRepo?.apiResponse()
        }
    }
    // Referansen til LiveData som inneholder API-responsen
    fun metAlertsLiveData() = mMetAlertsRepo?.metAlertsLiveData()

    // Setter referansen  i repo til riktig objekt
    fun setAlertData(alertClicked: AlertModel) {
        mAlertRepo?.setAlert(alertClicked)
    }

    // Referansen til LiveData som inneholder API-responsen
    fun alertLiveData() = mAlertRepo?.alertLiveData()

    fun setFullAlertsApiData(list: List<MetAlertModel>, userPosition: LatLng){
        if (mAlertRepo == null) {
            mAlertRepo = AlertRepository()
            mAlertRepo?.apiResponseFull(list, userPosition)
        }
    }
    fun fullAlertsLiveData() = mAlertRepo?.fullAlertListLiveData()
    // -----






    fun updateMetFullList(list: List<AlertModel>) {
        metFullList = list
    }

    fun setMetFullList():  List<AlertModel>{
        if (mMetAlertsRepo != null) metFullList = mAlertRepo?.fullAlertListLiveData()?.value!!
        return metFullList
    }

    // Sortere MetAlerts
    fun sortMetAlerts(pos: Int):MutableList<AlertModel>{
        var list = setMetFullList()
        when (pos) {
            1 -> list = metFullList.sortedWith(compareBy{it.info?.area?.areaDesc?.toLowerCase(
                Locale.ROOT) }).toMutableList()
            2 -> list = metFullList.sortedWith(compareBy { it.distance }).toMutableList()
            3 -> list = metFullList.sortedWith(compareBy { it.info?.parameter!!["awareness_level"]!!.split("; ")[0].toInt() }).reversed().toMutableList()
            4 -> list = metFullList.sortedWith(compareBy { it.info?.parameter!!["awareness_level"]!!.split("; ")[0].toInt() }).toMutableList()
        }
        metLastList = list as MutableList<AlertModel>
        mAlertRepo?.notifyLiveList(metLastList)
        return list
    }



}