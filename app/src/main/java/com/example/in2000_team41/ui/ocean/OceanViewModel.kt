package com.example.in2000_team41.ui.ocean

import androidx.lifecycle.ViewModel
import com.example.in2000_team41.api.oceanforecast.OceanForecastModel
import com.example.in2000_team41.api.oceanforecast.OceanForecastRepository

class OceanViewModel : ViewModel() {
    private var mOceanForecastRepo: OceanForecastRepository? = null
    var lastOceanForecastRespone: OceanForecastModel? = null
    var lastSearchPlace: String? = null
    var infoClicked = false

    fun setOceanForecastData(lat: Double, long: Double) {
        if (mOceanForecastRepo == null) mOceanForecastRepo = OceanForecastRepository()
        mOceanForecastRepo?.apiResponse(lat.toString(), long.toString())
    }
    // Referansen til LiveData som inneholder API-responsen
    fun oceanForecastData() = mOceanForecastRepo?.oceanForecastLiveData()


}