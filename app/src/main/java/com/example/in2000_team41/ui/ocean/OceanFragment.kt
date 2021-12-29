package com.example.in2000_team41.ui.ocean

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.in2000_team41.R
import com.example.in2000_team41.api.oceanforecast.OceanForecastModel
import com.example.in2000_team41.ui.home.HomeViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_ocean.*
import kotlinx.android.synthetic.main.fragment_ocean.updated_tv

class OceanFragment : Fragment() {

    private lateinit var viewModel: OceanViewModel
    private val viewModelHome: HomeViewModel by activityViewModels()
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_ocean, container, false)
        //for autocomplete-soek
        val apiKey = getString(R.string.autocomplete_api_key)
        //activity stuff (?)
        val activity = activity as Context
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey)
        }

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment_ocean) as AutocompleteSupportFragment
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES) // filtrerer etter by

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG
            )
        )

        // Resultatet viser kun norske steder -> blir ingen feilmeldinger/crash
        autocompleteFragment.setCountries("NO")

        // Sjekker når søket etter sted blir gjennomført
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                maincontent_ocean.visibility = View.GONE
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}")

                viewModel.setOceanForecastData(place.latLng!!.latitude, place.latLng!!.longitude)
                viewModel.oceanForecastData()!!.observe(viewLifecycleOwner, {data  ->
                    viewModel.lastOceanForecastRespone = data
                    viewModel.lastSearchPlace = place.name
                    maincontent_ocean.visibility = View.VISIBLE
                    setData(data, place.name!!)
                })
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = resources.getString(R.string.havfarevarsler)

        helpbutton_ocean.setOnClickListener {
            // Hindrer appen i å krasje dersom brukeren trykker to ganger raskt
            if (!viewModel.infoClicked) {
                findNavController().navigate(R.id.action_OceanFragment_to_BottomSheetOceaninfo)
                viewModel.infoClicked = true
            }
        }

        viewModel = ViewModelProvider(requireActivity()).get(OceanViewModel::class.java)

        if (viewModel.lastOceanForecastRespone != null) {
            maincontent_ocean.visibility = View.VISIBLE
            setData(viewModel.lastOceanForecastRespone!!, viewModel.lastSearchPlace!!)
        } else {
            viewModel.setOceanForecastData(viewModelHome.coordinates.latitude, viewModelHome.coordinates.longitude)
            viewModel.oceanForecastData()!!.observe(viewLifecycleOwner, {data  ->
                viewModel.lastOceanForecastRespone = data
                viewModel.lastSearchPlace = viewModelHome.userAdress
                maincontent_ocean.visibility = View.VISIBLE
                setData(data, viewModel.lastSearchPlace!!)
            })
        }
    }



    @SuppressLint("SetTextI18n")
    private fun setData(data: OceanForecastModel, place: String){
        oceandata_linearlayout.visibility = View.VISIBLE
        place_oceansearch.text = place
        if (data.properties.timeseries.isNotEmpty()){
            updated_tv.text = "Oppdatert: " + data.properties.meta!!.updated_at!!.split('T')[1].split('Z')[0]

            // Today
            val dateArray = data.properties.timeseries[0].time!!.split('T')[0].split('-')
            val mnd = dateArray[1]
            val day = dateArray[2]
            val mndString = getMnd(mnd)
            ocean_date_today.text = "I dag - $day. $mndString"
            ocean_time_today.text = "Kl. " +  data.properties.timeseries[0].time!!.split('T')[1].split(':')[0] + ":00"
            val baseToday1 = data.properties.timeseries[0].data!!.instant!!.details
            seatemp_today.text = baseToday1.sea_water_temperature.toString() + "°"
            waveheight_today.text = baseToday1.sea_surface_wave_height.toString() + "m"
            seawaterspeed_today.text = baseToday1.sea_water_speed.toString() + "km/t"
            // ---
            val nextTimeToday = data.properties.timeseries[11].time!!.split('T')[1].split(':')[0].toInt()
            if (nextTimeToday < 12){
                today2_linearlayout.visibility = View.GONE
                today2_bottomline.visibility = View.GONE
            } else {
                ocean_time_2today.text = "Kl. " +  data.properties.timeseries[11].time!!.split('T')[1].split(':')[0] + ":00"
                val base2Today = data.properties.timeseries[11].data!!.instant!!.details
                seatemp_2today.text = base2Today.sea_water_temperature.toString() + "°"
                waveheight_2today.text = base2Today.sea_surface_wave_height.toString() + "m"
                seawaterspeed_2today.text = base2Today.sea_water_speed.toString() + "km/t"
            }
            var nextIndex = 11+(24-nextTimeToday)

            // Tomorrow
            nextIndex += 6
            val dateArray2 = data.properties.timeseries[nextIndex].time!!.split('T')[0].split('-')
            val mnd2 = dateArray2[1]
            val day2 = dateArray2[2]
            val mndString2 = getMnd(mnd2)
            ocean_date_day2.text = "I morgen - $day2. $mndString2"
            ocean_time_day2.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val baseDay2 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_day2.text = baseDay2.sea_water_temperature.toString() + "°"
            waveheight_day2.text = baseDay2.sea_surface_wave_height.toString() + "m"
            seawaterspeed_day2.text = baseDay2.sea_water_speed.toString() + "km/t"
            // ---
            nextIndex += 12
            ocean_time_2day2.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val base2day2 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_2day2.text = base2day2.sea_water_temperature.toString() + "°"
            waveheight_2day2.text = base2day2.sea_surface_wave_height.toString() + "m"
            seawaterspeed_2day2.text = base2day2.sea_water_speed.toString() + "km/t"

            // Day 3
            nextIndex += 12
            val dateArray3 = data.properties.timeseries[nextIndex].time!!.split('T')[0].split('-')
            ocean_date_day3.text =  "${dateArray3[2]}. ${getMnd(dateArray3[1])}"
            ocean_time_day3.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val baseDay3 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_day3.text = baseDay3.sea_water_temperature.toString() + "°"
            waveheight_day3.text = baseDay3.sea_surface_wave_height.toString() + "m"
            seawaterspeed_day3.text = baseDay3.sea_water_speed.toString() + "km/t"
            // ---
            nextIndex += 12
            ocean_time_2day3.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val base2day3 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_2day3.text = base2day3.sea_water_temperature.toString() + "°"
            waveheight_2day3.text = base2day3.sea_surface_wave_height.toString() + "m"
            seawaterspeed_2day3.text = base2day3.sea_water_speed.toString() + "km/t"

            // Day 4
            nextIndex += 12
            val dateArray4 = data.properties.timeseries[nextIndex].time!!.split('T')[0].split('-')
            ocean_date_day4.text =  "${dateArray4[2]}. ${getMnd(dateArray4[1])}"
            ocean_time_day4.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val baseDay4 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_day4.text = baseDay4.sea_water_temperature.toString() + "°"
            waveheight_day4.text = baseDay4.sea_surface_wave_height.toString() + "m"
            seawaterspeed_day4.text = baseDay4.sea_water_speed.toString() + "km/t"
            // ---
            nextIndex += 12
            ocean_time_2day4.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val base2day4 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_2day4.text = base2day4.sea_water_temperature.toString() + "°"
            waveheight_2day4.text = base2day4.sea_surface_wave_height.toString() + "m"
            seawaterspeed_2day4.text = base2day4.sea_water_speed.toString() + "km/t"

            // Day 5
            nextIndex += 12
            val dateArray5 = data.properties.timeseries[nextIndex].time!!.split('T')[0].split('-')
            ocean_date_day5.text =  "${dateArray5[2]}. ${getMnd(dateArray5[1])}"
            ocean_time_day5.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val baseDay5 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_day5.text = baseDay5.sea_water_temperature.toString() + "°"
            waveheight_day5.text = baseDay5.sea_surface_wave_height.toString() + "m"
            seawaterspeed_day5.text = baseDay5.sea_water_speed.toString() + "km/t"
            // ---
            nextIndex += 12
            ocean_time_2day5.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val base2day5 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_2day5.text = base2day5.sea_water_temperature.toString() + "°"
            waveheight_2day5.text = base2day5.sea_surface_wave_height.toString() + "m"
            seawaterspeed_2day5.text = base2day5.sea_water_speed.toString() + "km/t"

            // Day 6
            nextIndex += 12
            val dateArray6 = data.properties.timeseries[nextIndex].time!!.split('T')[0].split('-')
            ocean_date_day6.text =  "${dateArray6[2]}. ${getMnd(dateArray6[1])}"
            ocean_time_day6.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val baseDay6 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_day6.text = baseDay6.sea_water_temperature.toString() + "°"
            waveheight_day6.text = baseDay6.sea_surface_wave_height.toString() + "m"
            seawaterspeed_day6.text = baseDay6.sea_water_speed.toString() + "km/t"
            // ---
            nextIndex += 12
            ocean_time_2day6.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val base2day6 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_2day6.text = base2day6.sea_water_temperature.toString() + "°"
            waveheight_2day6.text = base2day6.sea_surface_wave_height.toString() + "m"
            seawaterspeed_2day6.text = base2day6.sea_water_speed.toString() + "km/t"

            // Day 7
            nextIndex += 12
            val dateArray7 = data.properties.timeseries[nextIndex].time!!.split('T')[0].split('-')
            ocean_date_day7.text =  "${dateArray7[2]}. ${getMnd(dateArray7[1])}"
            ocean_time_day7.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val baseDay7 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_day7.text = baseDay7.sea_water_temperature.toString() + "°"
            waveheight_day7.text = baseDay7.sea_surface_wave_height.toString() + "m"
            seawaterspeed_day7.text = baseDay7.sea_water_speed.toString() + "km/t"
            // ---
            nextIndex += 12
            ocean_time_2day7.text = "Kl. " +  data.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0] + ":00"
            val base2day7 = data.properties.timeseries[nextIndex].data!!.instant!!.details
            seatemp_2day7.text = base2day7.sea_water_temperature.toString() + "°"
            waveheight_2day7.text = base2day7.sea_surface_wave_height.toString() + "m"
            seawaterspeed_2day7.text = base2day7.sea_water_speed.toString() + "km/t"
        } else {
            updated_tv.text = "Ikke et havområde / Mangler data"
            oceandata_linearlayout.visibility = View.GONE
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


}