package com.example.in2000_team41.ui.weather

import androidx.lifecycle.ViewModel
import com.example.in2000_team41.api.locationforecast.LocationForecastModel
import com.example.in2000_team41.api.locationforecast.LocationForecastRepository

class WeatherViewModel : ViewModel() {
    private var mLocationForecastRepo: LocationForecastRepository? = LocationForecastRepository()
    var lastLocationForecastResponse: LocationForecastModel? = null
    var lastSearchPlace: String? = null

    fun setLocationforecast(lat: Double, long: Double) {
        mLocationForecastRepo?.apiResponse(lat.toString(), long.toString())
    }
    fun getLocationForecast() = mLocationForecastRepo?.locationForecastData()


}