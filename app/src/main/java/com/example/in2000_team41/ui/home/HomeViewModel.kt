package com.example.in2000_team41.ui.home

import androidx.lifecycle.ViewModel
import com.example.in2000_team41.api.avalanche.AvalancheRepository
import com.example.in2000_team41.api.forestfire.ForestfireRepository
import com.example.in2000_team41.api.locationforecast.LocationForecastModel
import com.example.in2000_team41.api.locationforecast.LocationForecastRepository
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.api.metalerts.AlertRepository
import com.example.in2000_team41.api.metalerts.MetAlertModel
import com.example.in2000_team41.api.metalerts.MetAlertsRepository
import com.google.android.gms.maps.model.LatLng

class HomeViewModel : ViewModel(){
    lateinit var coordinates: LatLng
    var userAdress = ""
    var appFirstLoad = true

    // MetAlert variables
    private var mMetAlertsRepo: MetAlertsRepository? = null
    private var mAlertRepo: AlertRepository? = null
    var infoClicked = false
    var dataCached = false
    var metalertClicked = false

    // LocationForecast variables
    private var mLocationForecastRepo: LocationForecastRepository? = LocationForecastRepository()
    var lastLocationForecastResponse: LocationForecastModel? = null

    // Forestfire variablene
    private var mForestfireRepo: ForestfireRepository? = null

    //avalanche variabel
    private var mAvalancheRepo: AvalancheRepository? = null



    // MetAlerts relevant
    fun setMetAlertsApiData() {
        if (mMetAlertsRepo == null) mMetAlertsRepo = MetAlertsRepository()
        mMetAlertsRepo?.apiResponse()
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
        if (mAlertRepo == null) mAlertRepo = AlertRepository()
        mAlertRepo?.apiResponseFull(list, userPosition)
    }
    fun fullAlertsLiveData() = mAlertRepo?.fullAlertListLiveData()



    //LocationForecast relevant
    fun setLocationforecast(lat: Double, long: Double) {
        mLocationForecastRepo?.apiResponse(lat.toString(), long.toString())
    }
    fun getLocationForecast() = mLocationForecastRepo?.locationForecastData()


    // Forestfire relevant
    // Henter Forefire data fra API
    fun setForestfireApiData() {
        if (mForestfireRepo == null){
            mForestfireRepo = ForestfireRepository()
            mForestfireRepo?.apiResponse()
        }
    }
    fun forestfireLiveData() = mForestfireRepo?.forestfireLiveData()


    fun setAvalancheApiData() {
        if (mAvalancheRepo == null){
            mAvalancheRepo = AvalancheRepository()
            mAvalancheRepo?.apiResponse()
        }
    }
    // Referanser til LiveData som inneholder API-responsen
    fun AvalancheLiveData() = mAvalancheRepo?.avalancheLiveData()
}