package com.example.in2000_team41.ui.forestfire

import androidx.lifecycle.ViewModel
import com.example.in2000_team41.api.forestfire.ForestfireHolder
import com.example.in2000_team41.api.forestfire.ForestfireModel
import com.example.in2000_team41.api.forestfire.ForestfireRepository

class ForestfireViewModel : ViewModel() {

    // Forestfire variables
    var firstForestfireRecyclerview = true
    private var mForestfireRepo: ForestfireRepository? = null
    private var fullList = listOf<ForestfireModel>()
    private var sortedList = mutableListOf<ForestfireModel>()
    var onClickForestfireData: ForestfireHolder? = null
    var infoClicked = false
    var elementOnClick = false
    var dataCached = false


    // Forestfire relevant
    // Henter Forefire data fra API
    fun setForestfireApiData() {
        if (mForestfireRepo == null){       // trenger ikke hente data flere ganger, unødvendig bruk av ressurser
            mForestfireRepo = ForestfireRepository()
            mForestfireRepo?.apiResponse()
        }
    }
    // Referanser til LiveData som inneholder API-responsen
    fun forestfireLiveData() = mForestfireRepo?.forestfireLiveData()
    // -----


    // første Forestfire er i alfabetisk rekkefølge
    fun sortFirstInput(list: MutableList<ForestfireModel>): List<ForestfireModel>{
        for (i in list.indices) {
            list[i].locations = list[i].locations!!.sortedWith(compareBy { it.name }).toMutableList()
        }
        return list
    }

    fun setforestfireFullList(): List<ForestfireModel> {
        if (mForestfireRepo != null) fullList = mForestfireRepo?.getFullList()!!
        return fullList
    }

    fun updateFullList(list: List<ForestfireModel>) {
        fullList = list
    }
    // Sortere Forestfire med spinner
    fun sortForestfire(pos: Int) : List<ForestfireModel>{
        val list = setforestfireFullList()
        when (pos) {
            1 -> {
                for (i in list.indices) {
                    list[i].locations = fullList[i].locations!!.sortedWith(
                        compareBy { it.name }).toMutableList()
                }
            }
            2 -> {
                for (i in list.indices) {
                    list[i].locations = fullList[i].locations!!.sortedWith(
                        compareBy({ it.danger_index?.toIntOrNull() }, { it.name })).reversed().toMutableList()
                }
            }
            3 -> {
                for (i in list.indices) {
                    list[i].locations = fullList[i].locations!!.sortedWith(
                        compareBy({ it.danger_index?.toIntOrNull() }, { it.name })).toMutableList()
                }
            }
        }
        sortedList = list as MutableList<ForestfireModel>
        mForestfireRepo?.notifyLiveData(sortedList)

        return list
    }




}