package com.example.in2000_team41.ui.home

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.in2000_team41.MainActivity
import com.example.in2000_team41.R
import com.example.in2000_team41.api.avalanche.AvalancheModel
import com.example.in2000_team41.api.forestfire.ForestfireHolder
import com.example.in2000_team41.api.forestfire.ForestfireModel
import com.example.in2000_team41.api.forestfire.LocationFireWarning
import com.example.in2000_team41.api.locationforecast.LocationForecastModel
import com.example.in2000_team41.api.locationforecast.NextHoursDetails
import com.example.in2000_team41.api.locationforecast.Summary
import com.example.in2000_team41.api.locationforecast.TimeForecast
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.ui.dialogs.HomeLoadingDialog
import com.example.in2000_team41.ui.forestfire.ForestfireAdapter
import com.example.in2000_team41.ui.metalerts.MetAlertsAdapter
import com.example.in2000_team41.utilities.LocationHelper
import com.example.in2000_team41.ui.forestfire.ForestfireViewModel
import com.example.in2000_team41.ui.winter.AvalancheAdapter
import com.example.in2000_team41.ui.winter.WinterViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception
import kotlin.math.roundToInt


class HomeFragment : Fragment(R.layout.fragment_home),
    MetAlertsAdapter.OnItemClickListener,
    ForestfireAdapter.OnItemClickListener,
    AvalancheAdapter.OnItemClickListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelForestfire: ForestfireViewModel
    private lateinit var viewModelWinter: WinterViewModel
    private var mMetAlertsAdapter: MetAlertsAdapter? = null
    private var mForefireAdapter: ForestfireAdapter? = null
    private var mAvalancheAdapter: AvalancheAdapter? = null
    private var nextIndex = 0
    private lateinit var homeLoadingDialog: HomeLoadingDialog



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        viewModelForestfire = ViewModelProvider(requireActivity()).get(ForestfireViewModel::class.java)
        viewModelWinter = ViewModelProvider(requireActivity()).get(WinterViewModel::class.java)

        // Viser guiden når appen starter
        if (viewModel.appFirstLoad) {
            viewModel.appFirstLoad = false
            findNavController().navigate(R.id.navigation_guide1)
        } else{
            (activity as MainActivity?)?.showTopBar()
            (activity as MainActivity?)?.showBottomNav()

            homeLoadingDialog = HomeLoadingDialog(this.requireActivity())

            requireActivity().title = resources.getString(R.string.good_day) + " 👋"

            if (!viewModel.dataCached){
                viewModel.dataCached = true
                homeLoadingDialog.startLoading()
                Handler().postDelayed({
                    homeLoadingDialog.dismiss()
                }, 3000)
            }

            setListeners()
            // Ber om tilgang til brukerens posisjon og oppdaterer hjemsiden
            LocationHelper.instance.getLocationAccess(requireContext(), this)
            getPosition()
        }
    }






    private fun setListeners() {
        card_weatherAlerts.setOnClickListener {
            findNavController().navigate(R.id.navigation_weatherAlerts)
        }
        card_map.setOnClickListener {
            findNavController().navigate(R.id.navigation_map)
        }
        card_forestfire.setOnClickListener{
            findNavController().navigate(R.id.navigation_forestfire)
        }
        card_winter.setOnClickListener{
            findNavController().navigate(R.id.navigation_winter)
        }
        card_guide.setOnClickListener{
            findNavController().navigate(R.id.navigation_guide1)
        }

        winter_my_position.setOnClickListener {
            // Hindrer appen i å krasje dersom brukeren trykker to ganger raskt
            if (!viewModelWinter.elementOnClick) {
                viewModelWinter.fromMyPosition = true
                findNavController().navigate(R.id.action_HomeFragment_to_BottomSheetAvalanchesearch)
                viewModelWinter.elementOnClick = true
            }
        }
    }


    // Callback fra Android som kjøres når man får et svar om en bruker har godtatt/avslått tilgang til posisjon.
    // Når dette skjer oppdaterer vi kartets posisjon, i tilfelle brukeren ga appen tilgang til posisjon.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        LocationHelper.instance.handleRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getPosition()
    }


    // Funksjon som flytter kameraet på kartet til brukerens posisjon og skrur av/på blå markør av brukerens posisjon
    private fun getPosition(){
        LocationHelper.instance.getDeviceLocation(requireActivity()) {
            try {
                viewModel.coordinates = it
                setUpHomeFragment(viewModel.coordinates)
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }
    }


    private fun setUpHomeFragment(position: LatLng){
        if (position == LatLng(59.911491, 10.757933)) {         // Oslo
            viewModel.userAdress = "Oslo"
        } else {
            getCityName(position.latitude, position.longitude)  // Trenger kun å hente stedsnavn dersom bruker gir tilgang på posisjonen (ikke default Oslo)
        }

        setData()
    }



    // Finner navnet på stedet ut i fra coordinatene til brukerens posisjon
    private fun getCityName(lat: Double, long: Double){
        try {
            val geocoder = Geocoder(this.requireContext())
            val addresses = geocoder.getFromLocation(lat, long, 1)
            if (addresses.size > 0){
                if (addresses[0].subAdminArea != null){
                    if (addresses[0].subAdminArea.split(' ').size > 1){
                        if (addresses[0].subAdminArea.split(' ')[1] == "kommune"){
                            viewModel.userAdress = addresses[0].subAdminArea.split(' ')[0]
                        } else {
                            viewModel.userAdress = addresses[0].subAdminArea
                        }
                    } else {
                        viewModel.userAdress = addresses[0].subAdminArea
                    }
                } else if (addresses[0].featureName != null) {
                    if (addresses[0].featureName.split(' ').size > 1){
                        if (addresses[0].featureName.split(' ')[1] == "kommune"){
                            viewModel.userAdress = addresses[0].featureName.split(' ')[0]
                        } else {
                            viewModel.userAdress = addresses[0].featureName
                        }
                    } else {
                        viewModel.userAdress = addresses[0].featureName
                    }
                }
            }
        } catch (exception: Exception) {
            println("Exception was thrown: ${exception.message}")
        }
    }



    private fun setData(){
        setWeatherData()
        setMetAlertsAdapter()
        setForestfireAdapter()
        setAvalancheAdapter()
    }



    // onClick() for hver MetAlert
    override fun onItemClickMet(alertClicked: AlertModel) {
        viewModel.metalertClicked = true
        viewModel.setAlertData(alertClicked)
        findNavController().navigate(R.id.alertDetailFragment)
    }

    // setter adapteren til mMetAlertsAdapter og observerer data fra MetAlerts-API
    @SuppressLint("SetTextI18n")
    private fun setMetAlertsAdapter(){
        metalerts_home_tv.text = "Farevarsler nærme ${viewModel.userAdress}"

        if (mMetAlertsAdapter == null) mMetAlertsAdapter = MetAlertsAdapter(this, viewModel.userAdress)

        viewModel.setMetAlertsApiData()

        viewModel.metAlertsLiveData()!!.observe(viewLifecycleOwner, {dataList  ->
            //Hente detaljert info om MetAlerts
            viewModel.setFullAlertsApiData(dataList, viewModel.coordinates)
            viewModel.fullAlertsLiveData()!!.observe(viewLifecycleOwner, { alerts ->
                // tar kun med de som er mindre enn 50km fra brukerens posisjon
                val listClosest = mutableListOf<AlertModel>()
                for (alert in alerts){
                    if (alert.distance!! < 50.1) listClosest.add(alert)
                }
                mMetAlertsAdapter?.updateAdapter(listClosest.sortedWith(compareBy { it.distance }).toMutableList())   // sorterer, viser nærmeste farevarslet først
            })
        })
    }


    // Forestfire data
    private fun setForestfireAdapter(){
        // HENTE DATA
        if (mForefireAdapter == null) mForefireAdapter = ForestfireAdapter()
        recyclerview_home_forestfire.adapter = mForefireAdapter
        recyclerview_home_forestfire.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerview_home_forestfire.setHasFixedSize(true)
        // OBSERVERE DATA

        viewModel.setForestfireApiData()
        viewModel.forestfireLiveData()!!.observe(viewLifecycleOwner, { dataList ->
            // Setter data fra api response
            // SORTERE DATA
            for (i in dataList.indices) {
                dataList[i].locations = dataList[i].locations!!.sortedWith(
                    compareBy({ it.danger_index?.toIntOrNull() }, { it.name })).reversed().toMutableList()
            }

            // Legger til top 3 sted for hver dag (i dag, i morgen, 2 dager)
            val top3LocationsToday = mutableListOf<LocationFireWarning>()
            val top3LocationsTomorrow = mutableListOf<LocationFireWarning>()
            val top3LocationsTwodays = mutableListOf<LocationFireWarning>()
            for (i in dataList.indices){
                top3LocationsToday.add(dataList[0].locations?.get(i)!!)
                top3LocationsTomorrow.add(dataList[1].locations?.get(i)!!)
                top3LocationsTwodays.add(dataList[2].locations?.get(i)!!)
            }

            // ForestfireModel viser alle varslene for en dag
            // Lengde på Liste<Antall Sted> forkortes til top 3 på hjemsiden
            val listForestfire = mutableListOf<ForestfireModel>()
            listForestfire.add(ForestfireModel(null, top3LocationsToday))
            listForestfire.add(ForestfireModel(null, top3LocationsTomorrow))
            listForestfire.add(ForestfireModel(null, top3LocationsTwodays))
            // SENDE DATA TIL VIEWS
            mForefireAdapter?.updateAdapter(listForestfire)
            recyclerview_home_forestfire.adapter?.notifyDataSetChanged()
        })
    }
    // Oppretter ViewModelForestfire her, og deler den med ForestfireFragment senere
    // Trenger ViewModelForestfire for onClick() Forestfire på hjemsiden
    override fun onItemClickForestfire(data: ForestfireHolder) {
        viewModelForestfire.onClickForestfireData = data
        // Hindrer appen i å krasje dersom brukeren trykker to ganger raskt
        if (!viewModelForestfire.elementOnClick) {
            viewModelForestfire.elementOnClick = true
        }
    }

    private fun setAvalancheAdapter(){
        if (mAvalancheAdapter == null) mAvalancheAdapter = AvalancheAdapter(this)
        recyclerview_home_winter.adapter = mAvalancheAdapter
        recyclerview_home_winter.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerview_home_winter.setHasFixedSize(true)

        viewModel.setAvalancheApiData()

        viewModel.AvalancheLiveData()!!.observe(viewLifecycleOwner, {dataList  ->
            var list = dataList
            list = list.sortedWith(
                compareBy( { it.AvalancheWarningList[0].DangerLevel?.toIntOrNull() }, { it.AvalancheWarningList[1].DangerLevel?.toIntOrNull() },{ it.AvalancheWarningList[2].DangerLevel?.toIntOrNull() })).reversed().toMutableList()

            val listAvalanche = mutableListOf<AvalancheModel>()
            listAvalanche.add(list[0])
            listAvalanche.add(list[1])
            listAvalanche.add(list[2])

            mAvalancheAdapter?.updateAdapter(listAvalanche)
            recyclerview_home_winter.adapter?.notifyDataSetChanged()
        })
    }

    override fun onItemClickAvalancheSummary(current: AvalancheModel) {
        viewModelWinter.currentData = current
        viewModelWinter.fromOnClick = true
        if (!viewModelWinter.elementOnClick) {
            findNavController().navigate(R.id.action_HomeFragment_to_BottomSheetAvalanchesearch)
            viewModelWinter.elementOnClick = true
        }
    }



    private fun setWeatherData() {
        nextIndex = 0
        if (viewModel.lastLocationForecastResponse == null) {
            // viser været i brukerens position
            viewModel.setLocationforecast(viewModel.coordinates.latitude, viewModel.coordinates.longitude)

            viewModel.getLocationForecast()!!.observe(viewLifecycleOwner, { data ->
                // Setter vær-data til Views
                viewModel.lastLocationForecastResponse = data
                today(data)
            })
        } else{
            today(viewModel.lastLocationForecastResponse!!)
        }
    }

    // __________________
    // Setter LocationForecast data til Views

    @SuppressLint("SetTextI18n")
    private fun today(weatherData: LocationForecastModel) {
        today1(weatherData)

        nextIndex += 6
        var nextTimeToday = weatherData.properties.timeseries[nextIndex].time!!.split('T')[1].split(
            ':'
        )[0].toInt()
        if (nextTimeToday in 17 downTo 11){
            home_content_today2.visibility = View.VISIBLE
            today2(weatherData)
        }else {
            home_content_today2.visibility = View.GONE
        }
        nextIndex += 6
        nextTimeToday = weatherData.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0].toInt()
        if (nextTimeToday in 23 downTo 18){
            home_content_today3.visibility = View.VISIBLE
            today3(weatherData)
        }else {
            home_content_today3.visibility = View.GONE
        }
        nextIndex += (24 - nextTimeToday)
    }

    //today
    @SuppressLint("SetTextI18n")
    private fun today1(weatherData: LocationForecastModel) {
        val base = weatherData.properties.timeseries[nextIndex]
        val dateArray = base.time?.split('T')?.get(0)!!.split('-')
        val mnd = dateArray[1]
        val day = dateArray[2]
        val mndString = getMnd(mnd)
        home_date_today.text = "I dag - $day. $mndString"

        setWeatherTextToday(base, home_time_today, home_temperature_today, home_icon_today, home_rain_today, home_wind_today)
    }


    @SuppressLint("SetTextI18n")
    private fun today2(weatherData: LocationForecastModel) {
        val base = weatherData.properties.timeseries[nextIndex]
        setWeatherTextToday(base, home_time_today2, home_temperature_today2, home_icon_today2, home_rain_today2, home_wind_today2)
    }

    @SuppressLint("SetTextI18n")
    private fun today3(weatherData: LocationForecastModel) {
        val base = weatherData.properties.timeseries[nextIndex]
        setWeatherTextToday(base, home_time_today3, home_temperature_today3, home_icon_today3, home_rain_today3, home_wind_today3)
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

    // binder data til layout
    @SuppressLint("SetTextI18n")
    fun setWeatherTextToday(base: TimeForecast, time: TextView, temp: TextView, icon: ImageView, rain: TextView, wind: TextView) {
        time.text = "Kl. " + base.time?.split('T')?.get(1)!!.split(':')[0] + ":00"
        temp.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"

        val summary: Summary?
        val details: NextHoursDetails?
        val data = base.data

        when {
            data.next_1_hours != null -> {
                summary = data.next_1_hours.summary
                details = data.next_1_hours.details
            }
            data.next_6_hours != null -> {
                summary = data.next_6_hours.summary
                details = data.next_6_hours.details
            }
            else -> {
                summary = data.next_12_hours?.summary
                details = data.next_12_hours?.details
            }
        }


        when (summary?.symbol_code) {
            "clearsky_day" -> icon.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon.setImageResource(R.drawable.weather_cloudy)
        }
        when (details?.precipitation_amount_max) {
            "0.0" -> {
                rain.text = "0"
            }
            null -> {
                rain.text = getString(R.string.unknown)
            }
            else -> {
                rain.text = "${details.precipitation_amount_min}-${details.precipitation_amount_max}"
            }
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }



}