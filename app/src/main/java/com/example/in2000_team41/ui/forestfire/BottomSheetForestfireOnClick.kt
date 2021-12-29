package com.example.in2000_team41.ui.forestfire

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.in2000_team41.R
import com.example.in2000_team41.api.forestfire.ForestfireHolder
import com.example.in2000_team41.ui.home.HomeViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.bottom_sheet_forestfire_onclick.*
import kotlinx.android.synthetic.main.bottom_sheet_forestfire_onclick.distanceFromPosition_tv
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetForestfireOnClick : BottomSheetDialogFragment() {

    // deler dataen fra ViewModel mellom fragmentene
    private val viewModel: ForestfireViewModel by activityViewModels()
    private val viewModelHome: HomeViewModel by activityViewModels()
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_forestfire_onclick, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // rounded corners
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geocoder = Geocoder(this.requireContext())
        if (viewModel.onClickForestfireData != null){
            setData(viewModel.onClickForestfireData!!)
            forestfire_onclick_linearlayout.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Kan ikke trykke igjen før BottomSheet lukkes - hindrer krasj
        viewModel.elementOnClick = false
    }




    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setData(data: ForestfireHolder){
        forestfire_onclick_place_tv.text = data.today!!.name

        // Regner ut og viser avstanden mellom stedet og brukerens posisjon
        try {
            var currentItemCoordinates: LatLng? = null
            val addresses = geocoder.getFromLocationName(data.today.name, 1)
            if (addresses.size > 0){
                currentItemCoordinates = LatLng(addresses[0].latitude, addresses[0].longitude)
            }
            if (currentItemCoordinates != null){
                val distance = SphericalUtil.computeDistanceBetween(viewModelHome.coordinates, currentItemCoordinates)/1000
                distanceFromPosition_tv.text = String.format("%.2f", distance) + "km fra " + viewModelHome.userAdress
            } else {
                distanceFromPosition_tv.text = "Klarte ikke finne avstanden fra " + viewModelHome.userAdress
            }
        } catch (exception: Exception) {
            distanceFromPosition_tv.text = "Klarte ikke finne avstanden fra " + viewModelHome.userAdress
            println("Exception was thrown: ${exception.message}")
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val dateArray = currentDate.split('/')
        val date1 = "${dateArray[0]}. ${getMnd(dateArray[1])}"

        date1_forestfire_onclick_tv.text = "I dag - $date1"
        val dangerlevel1 = simplifyDangerlevel(data.today.danger_index!!)
        warning1_forestfire_onclick_tv.setBackgroundColor(Color.parseColor(getColor(dangerlevel1)))
        warning1_forestfire_onclick_tv.text = dangerlevel1
        text1_forestfire_onclick_tv.text = getDangerText(dangerlevel1)

        val date2 = nextDate(date1)
        date2_forestfire_onclick_tv.text = "I morgen - $date2"
        val dangerlevel2 = simplifyDangerlevel(data.tomorrow!!.danger_index!!)
        warning2_forestfire_onclick_tv.setBackgroundColor(Color.parseColor(getColor(dangerlevel2)))
        warning2_forestfire_onclick_tv.text = dangerlevel2
        text2_forestfire_onclick_tv.text = getDangerText(dangerlevel2)

        val date3 = nextDate(date2)
        date3_forestfire_onclick_tv.text = "Om 2 dager - $date3"
        val dangerlevel3 = simplifyDangerlevel(data.twodays!!.danger_index!!)
        warning3_forestfire_onclick_tv.setBackgroundColor(Color.parseColor(getColor(dangerlevel3)))
        warning3_forestfire_onclick_tv.text = dangerlevel3
        text3_forestfire_onclick_tv.text = getDangerText(dangerlevel3)
    }



    private fun getDangerText(dangerLevel: String): String {
        return when (dangerLevel.toIntOrNull()){
            0 -> "Minial fare"
            1 -> "Liten fare"
            2 -> "Moderat fare"
            3 -> "Betydelig fare"
            4 -> "Stor fare"
            5 -> "Meget stor fare"
            else -> "Ukjent"
        }
    }

    private fun simplifyDangerlevel(i: String): String{
        return when(i.toIntOrNull()) {
            0 -> "0"
            in 1..3 -> "1"
            in 4..10 -> "2"
            in 11..29 -> "3"
            in 30..45 -> "4"
            in 46..Int.MAX_VALUE -> "5"
            else -> "-"
        }
    }


    private fun getColor(i: String): String{
        return when(i.toIntOrNull()) {
            in 0..1 -> "#00ab2e"
            2 -> "#fff019"
            3 -> "#ff9f05"
            in 4..5 -> "#ff4f19"
            else -> "#b8b8b8"
        }
    }

    private fun getMnd(mnd: String): String {
        return when (mnd) {
            "01" -> "januar"
            "02" -> "februar"
            "03" -> "mars"
            "04" -> "april"
            "05" -> "mai"
            "06" -> "juni"
            "07" -> "juli"
            "08" -> "august"
            "09" -> "september"
            "10" -> "oktober"
            "11" -> "november"
            "12" -> "desember"
            else -> ""
        }
    }
    private fun getNextMnd(currentMnd: String): String {
        return when (currentMnd){
            "januar" -> "februar"
            "februar" -> "mars"
            "mars" -> "april"
            "april" -> "mai"
            "mai" -> "juni"
            "juni" -> "juli"
            "juli" -> "august"
            "august" -> "september"
            "september" -> "oktober"
            "oktober" -> "november"
            "november" -> "desember"
            "desember" -> "januar"
            else -> "ERROR"
        }
    }

    private fun nextDate(currentDate: String): String {
        val currentDay = currentDate.split(". ")[0]
        val currentMnd = currentDate.split(". ")[1]
        val nextDay: String
        val nextMnd: String
        if ((currentDay.toInt() == 28)
            && (currentDate.split(". ")[1] == "februar")){
            nextDay = "1"
            nextMnd = getNextMnd(currentDate.split(". ")[1])
        }
        else if ((currentDay.toInt() == 30)
            && ((currentDate.split(". ")[1] == "april")
                    || (currentDate.split(". ")[1] == "juni")
                    || (currentDate.split(". ")[1] == "september")
                    || (currentDate.split(". ")[1] == "november"))){
            nextDay = "1"
            nextMnd = getNextMnd(currentDate.split(". ")[1])
        }
        else if (currentDay.toInt() == 31){
            nextDay = "1"
            nextMnd = getNextMnd(currentDate.split(". ")[1])
        } else {
            val day = currentDay.toInt()
            nextDay = "${day+1}"
            nextMnd = currentMnd
        }
        return "$nextDay. $nextMnd"
    }



}