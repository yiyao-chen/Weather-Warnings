package com.example.in2000_team41.ui.alertdetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team41.R
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.ui.home.HomeViewModel
import com.example.in2000_team41.ui.map.MapViewModel
import kotlinx.android.synthetic.main.fragment_alert_detail_map.awarenessLevel_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.awarenessLevel_v
import kotlinx.android.synthetic.main.fragment_alert_detail_map.awarenessSeriousness_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.certainty_image
import kotlinx.android.synthetic.main.fragment_alert_detail_map.certainty_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.consequences_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.description_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.distanceFromPosition_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.eventAwarenessName_image
import kotlinx.android.synthetic.main.fragment_alert_detail_map.eventAwarenessName_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.instruction_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.location_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.timeEnd_tv
import kotlinx.android.synthetic.main.fragment_alert_detail_map.timeStart_tv
import java.util.*

class AlertDetailFragmentMap : Fragment(R.layout.fragment_alert_detail_map) {
    private lateinit var viewModel: MapViewModel
    private val viewModelHome: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "Farevarsel"
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        viewModel.alertLiveData()?.observe(viewLifecycleOwner, {
            setText(it)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setText(it: AlertModel){
        location_tv.text = "Omr??de: " + it.info?.area?.areaDesc
        distanceFromPosition_tv.text = String.format("%.2f", it.distance) + "km fra " + viewModelHome.userAdress
        description_tv.text = it.info?.description

        val timeStartString = it.info?.onset!!.split(':')[0]
        val timeStart = timeStartString.split('T')[1]
        val dateStartArray= timeStartString.split('T')[0].split('-')
        val dateStart = "${dateStartArray[2]}. ${getMnd(dateStartArray[1])}"
        timeStart_tv.text = "$dateStart - Kl. $timeStart:00"

        val timeEndString = it.info?.expires!!.split(':')[0]
        val timeEnd = timeEndString.split('T')[1]
        val dateEndArray= timeStartString.split('T')[0].split('-')
        val dateEnd = "${dateEndArray[2]}. ${getMnd(dateEndArray[1])}"
        timeEnd_tv.text = "$dateEnd - Kl. $timeEnd:00"

        when (it.info?.parameter?.get("eventAwarenessName")?.toLowerCase(Locale.ROOT)) {
            "kuling" -> eventAwarenessName_image.setImageResource(R.drawable.weather_wind)
            "storm" -> eventAwarenessName_image.setImageResource(R.drawable.weather_storm)
            "sterk ising p?? skip" -> eventAwarenessName_image.setImageResource(R.drawable.symbol_cold)
            "sn??" -> eventAwarenessName_image.setImageResource(R.drawable.weather_snow)
            "kraftig sn??fokk" -> eventAwarenessName_image.setImageResource(R.drawable.weather_snow)
            "skogbrannfare" -> eventAwarenessName_image.setImageResource(R.drawable.img_forestfire)
            else -> eventAwarenessName_image.setImageResource(R.drawable.weather_warning)
        }

        eventAwarenessName_tv.text = it.info?.parameter!!["eventAwarenessName"]

        certainty_tv.text = it.info?.certainty
        if (it.info?.certainty!!.toLowerCase(Locale.ROOT) == "likely") {
            certainty_image.setImageResource(R.drawable.symbol_likely)
        } else {
            certainty_image.setImageResource(R.drawable.symbol_unlikely)
        }

        val awarenessLevel = it.info?.parameter!!["awareness_level"]!!.split("; ")
        awarenessLevel_tv.text = awarenessLevel[0] + " - " + awarenessLevel[2]
        val colorCode = when(awarenessLevel[1]) {
            "green" -> "#00ab2e"
            "yellow" -> "#fff019"
            "orange" -> "#ff9f05"
            "red" -> "#ff4f19"
            else -> "#b8b8b8"
        }
        awarenessLevel_v.setBackgroundColor(Color.parseColor(colorCode))

        awarenessSeriousness_tv.text = it.info?.parameter!!["awarenessSeriousness"]

        instruction_tv.text = it.info?.instruction
        consequences_tv.text = it.info?.parameter!!["consequences"]
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


}


