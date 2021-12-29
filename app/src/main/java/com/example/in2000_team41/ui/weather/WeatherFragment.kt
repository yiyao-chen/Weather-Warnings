package com.example.in2000_team41.ui.weather

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team41.R
import com.example.in2000_team41.api.locationforecast.LocationForecastModel
import com.example.in2000_team41.api.locationforecast.NextHoursDetails
import com.example.in2000_team41.api.locationforecast.Summary
import com.example.in2000_team41.ui.home.HomeViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlin.math.roundToInt


class WeatherFragment : Fragment() {

    private lateinit var placesClient: PlacesClient
    private lateinit var viewModel: WeatherViewModel
    private val viewModelHome: HomeViewModel by activityViewModels()
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var nextIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_weather, container, false)
        //for autocomplete-soek
        val apiKey = getString(R.string.autocomplete_api_key)
        //activity stuff (?)
        val activity = activity as Context
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey)
        }
        placesClient = Places.createClient(this.requireContext())

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment_ocean) as AutocompleteSupportFragment
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES) // filtrerer etter by
        autocompleteFragment.setHint(resources.getString(R.string.search_for_weatherforecast))

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
            @SuppressLint("SetTextI18n")
            override fun onPlaceSelected(place: Place) {
                searchhelp_tv.visibility = View.GONE
                maincontent_weather.visibility = View.GONE
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}")

                viewModel.setLocationforecast(place.latLng!!.latitude, place.latLng!!.longitude)
                viewModel.getLocationForecast()!!.observe(viewLifecycleOwner, { data ->
                    // Setter vær-data til Views
                    viewModel.lastLocationForecastResponse = data
                    viewModel.lastSearchPlace = place.name
                    maincontent_weather.visibility = View.VISIBLE
                    nextIndex = 0
                    place_weathersearch.text = place.name
                    updated_tv.text =
                        "Oppdatert: " + data.properties.meta!!.updated_at!!.split('T')[1].split(
                            'Z'
                        )[0]
                    today(data)
                    tomorrow(data)
                    twodays(data)
                })
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })

        return view
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = resources.getString(R.string.v_rvarsel)
        searchhelp_tv.visibility = View.GONE

        viewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)

        if (viewModel.lastLocationForecastResponse != null) {
            maincontent_weather.visibility = View.VISIBLE
            place_weathersearch.text = viewModel.lastSearchPlace
            updated_tv.text =
                "Oppdatert: " + viewModel.lastLocationForecastResponse!!.properties.meta!!.updated_at!!.split(
                    'T'
                )[1].split('Z')[0]
            today(viewModel.lastLocationForecastResponse!!)
            tomorrow(viewModel.lastLocationForecastResponse!!)
            twodays(viewModel.lastLocationForecastResponse!!)
        } else {
            // Setter den første visningen for været
            viewModel.setLocationforecast(viewModelHome.coordinates.latitude, viewModelHome.coordinates.longitude)
            viewModel.getLocationForecast()!!.observe(viewLifecycleOwner, { data ->
                // Setter vær-data til Views
                viewModel.lastLocationForecastResponse = data
                viewModel.lastSearchPlace = viewModelHome.userAdress
                maincontent_weather.visibility = View.VISIBLE
                nextIndex = 0
                today(data)
                tomorrow(data)
                twodays(data)
            })
        }
    }




    // __________________
    // Setter LocationForecast data til Views

    @SuppressLint("SetTextI18n")
    private fun today(weatherData: LocationForecastModel) {
        place_weathersearch.text = viewModel.lastSearchPlace
        updated_tv.text = "Oppdatert: " + weatherData.properties.meta!!.updated_at!!.split('T')[1].split('Z')[0]

        today1(weatherData)
        nextIndex += 6
        var nextTimeToday = weatherData.properties.timeseries[nextIndex].time!!.split('T')[1].split(
            ':'
        )[0].toInt()
        if (nextTimeToday in 23 downTo 3){
            content_today2.visibility = View.VISIBLE
            line_today2.visibility = View.VISIBLE
            today2(weatherData)
        } else {
            content_today2.visibility = View.GONE
            line_today2.visibility = View.GONE
        }
        nextIndex += 6
        nextTimeToday = weatherData.properties.timeseries[nextIndex].time!!.split('T')[1].split(':')[0].toInt()
        if (nextTimeToday in 23 downTo 18){
            content_today3.visibility = View.VISIBLE
            line_today3.visibility = View.VISIBLE
            today3(weatherData)
        } else {
            content_today3.visibility = View.GONE
            line_today3.visibility = View.GONE
        }
        nextIndex += (24 - nextTimeToday)
    }

    //today
    @SuppressLint("SetTextI18n")
    private fun today1(weatherData: LocationForecastModel) {
        val base = weatherData.properties.timeseries[nextIndex]
        time_today.text = "Kl. " + base.time!!.split('T')[1].split(':')[0] + ":00"
        temperature_today.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
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
            "clearsky_day" -> icon_today.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_today.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_today.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_today.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_today.text = "0"
        } else {
            rain_today.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_today.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_today.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun today2(weatherData: LocationForecastModel) {
        val base = weatherData.properties.timeseries[nextIndex]
        time_today2.text = "Kl. " + base.time?.split('T')?.get(1)!!.split(':')[0] + ":00"
        temperature_today2.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
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
            "clearsky_day" -> icon_today2.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_today2.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_today2.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_today2.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_today2.text = "0"
        } else {
            rain_today2.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_today2.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_today2.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun today3(weatherData: LocationForecastModel) {
        val base = weatherData.properties.timeseries[nextIndex]
        time_today3.text = "Kl. " + base.time?.split('T')?.get(1)!!.split(':')[0] + ":00"
        temperature_today3.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
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
            "clearsky_day" -> icon_today3.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_today3.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_today3.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_today3.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_today3.text = "0"
        } else {
            rain_today3.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        rain_tomorrow3.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_today3.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_today3.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }



    //tomorrow
    @SuppressLint("SetTextI18n")
    private fun tomorrow(weatherData: LocationForecastModel) {
        nextIndex += 6
        val base = weatherData.properties.timeseries[nextIndex]
        time_tomorrow.text = "Kl. " + base.time!!.split('T')[1].split(':')[0] + ":00"
        temperature_tomorrow.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
        val summary: Summary?
        val details: NextHoursDetails?
        val data = base.data
        // sjekker hvilken av time-datanene som er tilgjengelig
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
            "clearsky_day" -> icon_tomorrow.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_tomorrow.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_tomorrow.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_tomorrow.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_tomorrow.text = "0"
        } else {
            rain_tomorrow.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_tomorrow.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_tomorrow.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
        tomorrow2(weatherData)
        tomorrow3(weatherData)
    }
    // 2
    @SuppressLint("SetTextI18n")
    private fun tomorrow2(weatherData: LocationForecastModel) {
        nextIndex += 6
        val base = weatherData.properties.timeseries[nextIndex]
        time_tomorrow2.text = "Kl. " + base.time?.split('T')?.get(1)!!.split(':')[0] + ":00"
        temperature_tomorrow2.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
        val summary: Summary?
        val details: NextHoursDetails?
        val data = base.data
        // sjekker hvilken av time-datanene som er tilgjengelig
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
            "clearsky_day" -> icon_tomorrow2.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_tomorrow2.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_tomorrow2.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_tomorrow2.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_tomorrow2.text = "0"
        } else {
            rain_tomorrow2.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_tomorrow2.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_tomorrow2.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }
    // 3
    @SuppressLint("SetTextI18n")
    private fun tomorrow3(weatherData: LocationForecastModel) {
        nextIndex += 6
        val base = weatherData.properties.timeseries[nextIndex]
        time_tomorrow3.text = "Kl. " + base.time?.split('T')?.get(1)!!.split(':')[0] + ":00"
        temperature_tomorrow3.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
        val summary: Summary?
        val details: NextHoursDetails?
        val data = base.data
        // sjekker hvilken av time-datanene som er tilgjengelig
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
            "clearsky_day" -> icon_tomorrow3.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_tomorrow3.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_tomorrow3.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_tomorrow3.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_tomorrow3.text = "0"
        } else {
            rain_tomorrow3.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_tomorrow3.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_tomorrow3.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }


    //twodays
    @SuppressLint("SetTextI18n")
    private fun twodays(weatherData: LocationForecastModel) {
        nextIndex += 12
        val base = weatherData.properties.timeseries[nextIndex]
        time_twodays.text = "Kl. " + base.time!!.split('T')[1].split(':')[0] + ":00"
        temperature_twodays.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
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
            "clearsky_day" -> icon_twodays.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_twodays.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_twodays.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_twodays.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_twodays.text = "0"
        } else {
            rain_twodays.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_twodays.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_twodays.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
        twodays2(weatherData)
        twodays3(weatherData)
    }
    // 2
    @SuppressLint("SetTextI18n")
    private fun twodays2(weatherData: LocationForecastModel) {
        nextIndex += 6
        val base = weatherData.properties.timeseries[nextIndex]
        time_twodays2.text = "Kl. " + base.time?.split('T')?.get(1)!!.split(':')[0] + ":00"
        temperature_twodays2.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
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
            "clearsky_day" -> icon_twodays2.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_twodays2.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_twodays2.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_twodays2.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_twodays2.text = "0"
        } else {
            rain_twodays2.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_twodays2.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_twodays2.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }
    // 3
    @SuppressLint("SetTextI18n")
    private fun twodays3(weatherData: LocationForecastModel) {
        nextIndex += 1 // til 18 kl (ikke flere timevarsler)
        val base = weatherData.properties.timeseries[nextIndex]
        time_twodays3.text = "Kl. " + base.time?.split('T')?.get(1)!!.split(':')[0] + ":00"
        temperature_twodays3.text = base.data?.instant?.details?.air_temperature!!.toDouble().roundToInt().toString()+"°"
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
            "clearsky_day" -> icon_twodays3.setImageResource(R.drawable.weather_sun)
            "clearsky_night" -> icon_twodays3.setImageResource(R.drawable.weather_moon)
            "partlycloudy_day" -> icon_twodays3.setImageResource(R.drawable.weather_partly_cloudy)
            else -> icon_twodays3.setImageResource(R.drawable.weather_cloudy)
        }
        if (details?.precipitation_amount_max == "0.0") {
            rain_twodays3.text = "0"
        } else {
            rain_twodays3.text = "${details?.precipitation_amount_min}-${details?.precipitation_amount_max}"
        }
        if (base.data.instant.details.wind_speed_of_gust != null) {
            wind_twodays3.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed_of_gust})"
        } else {
            wind_twodays3.text = "${base.data.instant.details.wind_speed} (${base.data.instant.details.wind_speed})"
        }
    }
}