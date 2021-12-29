package com.example.in2000_team41.ui.winter

import androidx.lifecycle.ViewModel
import com.example.in2000_team41.api.avalanche.*

class WinterViewModel : ViewModel() {
    private var mAvalancheRepo: AvalancheRepository? = null
    private var mAvalancheSimpleRepo = AvalancheSimpleRepository()
    var firstClick = true
    var lastSearchPlace: String? = null
    var currentData: AvalancheModel? = null
    var fromMyPosition = false
    var fromOnClick = false
    var infoClicked = false
    var elementOnClick = false
    var dataCached = false
    private var fullList = listOf<AvalancheModel>()
    private var lastList = mutableListOf<AvalancheModel>()



    // ----------
    fun setAvalancheRegionSummary() {
        if (mAvalancheRepo == null){        // trenger ikke hente data flere ganger, unødvendig bruk av ressurser
            mAvalancheRepo = AvalancheRepository()
            mAvalancheRepo?.apiResponse()
        }
    }
    // Referansen til LiveData som inneholder API-responsen
    fun avalancheRegionSummaryLiveData() = mAvalancheRepo?.avalancheLiveData()


    // ----------
    fun setAvalancheSimpleRegion(latitude: String, longitude: String) {
        mAvalancheSimpleRepo.apiResponse(latitude, longitude)  // trenger kun et repository objekt, men gjør ulike api-kall
    }
    fun avalancheRegionSimpleRegionLiveData() = mAvalancheSimpleRepo.avalancheLiveData()

    fun getDataFromEarlierResponse() = mAvalancheSimpleRepo.getFullListData()

    fun setFullList(): List<AvalancheModel> {
        if (mAvalancheRepo != null) fullList = mAvalancheRepo?.getFullList()!!
        return fullList
    }

    fun updateFullList(list: List<AvalancheModel> ) {
        fullList = list
    }
    // Sortere Avalanche med spinner
    fun sortAvalanche(pos: Int) : List<AvalancheModel>{
        var list = setFullList()

        when (pos) {
            // alfabetisk
            1 -> list = list.sortedWith(
                compareBy { it.Name }).toMutableList()

            // descending
            2 -> list = list.sortedWith(
                    compareBy( { it.AvalancheWarningList[0].DangerLevel?.toIntOrNull() }, { it.AvalancheWarningList[1].DangerLevel?.toIntOrNull() },{ it.AvalancheWarningList[2].DangerLevel?.toIntOrNull() })).reversed().toMutableList()

            // ascending
            3 -> list = list.sortedWith(
                    compareBy( { it.AvalancheWarningList[0].DangerLevel?.toIntOrNull() }, { it.AvalancheWarningList[1].DangerLevel?.toIntOrNull() },{ it.AvalancheWarningList[2].DangerLevel?.toIntOrNull() })).toMutableList()

        }
        lastList = list as MutableList<AvalancheModel>
        mAvalancheRepo?.notifyLiveData(lastList)
        return list
    }
}