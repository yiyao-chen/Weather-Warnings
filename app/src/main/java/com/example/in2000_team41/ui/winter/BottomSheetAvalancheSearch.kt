package com.example.in2000_team41.ui.winter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.in2000_team41.R
import com.example.in2000_team41.api.avalanche.AvalancheWarning
import com.example.in2000_team41.ui.home.HomeViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.bottom_sheet_avalanchesearch.*
import kotlinx.android.synthetic.main.bottom_sheet_avalanchesearch.distanceFromPosition_tv
import java.lang.Exception


class BottomSheetAvalancheSearch : BottomSheetDialogFragment() {

    // deler dataen fra ViewModel mellom fragmentene
    private val viewModel: WinterViewModel by activityViewModels()
    private val viewModelHome: HomeViewModel by activityViewModels()
    private lateinit var geocoder: Geocoder
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // rounded corners
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.bottom_sheet_avalanchesearch, container, false)
        //for autocomplete-soek
        val apiKey = getString(R.string.autocomplete_api_key)
        //activity stuff (?)
        val activity = activity as Context
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey)
        }

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment_avalanche) as AutocompleteSupportFragment
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
                viewModel.fromOnClick = false
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}")
                viewModel.lastSearchPlace = place.name

                val string = place.latLng.toString().split('(')[1].split(',')
                val lat = string[0]
                val long = string[1].split(')')[0]

                viewModel.setAvalancheSimpleRegion(lat, long)
                viewModel.avalancheRegionSimpleRegionLiveData().observe(viewLifecycleOwner, {dataList  ->
                    setData(dataList, null)
                    avalanchesearch_data_linearlayout.visibility = View.VISIBLE
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
        viewModel.lastSearchPlace = viewModelHome.userAdress

        geocoder = Geocoder(this.requireContext())

        if (viewModel.fromOnClick) {
            setData(viewModel.currentData!!.AvalancheWarningList, viewModel.currentData!!.TypeName)
            avalanchesearch_data_linearlayout.visibility = View.VISIBLE
            viewModel.fromOnClick = false
        } else if (viewModel.firstClick || viewModel.fromMyPosition) {
            // Om dataen er fra onClick fra listen (adapter)
            // første visning // varsel min posisjon
            val lat = viewModelHome.coordinates.latitude.toString()
            val long = viewModelHome.coordinates.longitude.toString()
            viewModel.setAvalancheSimpleRegion(lat, long)
            viewModel.avalancheRegionSimpleRegionLiveData().observe(viewLifecycleOwner, { dataList ->
                setData(dataList, null)
                avalanchesearch_data_linearlayout.visibility = View.VISIBLE
            })
            viewModel.fromMyPosition = false
        } else {
        // Sjekker om det allerede er data tilgjengelig
            val previousResponse = viewModel.getDataFromEarlierResponse()
            if (previousResponse.isNotEmpty()){
                setData(previousResponse, null)
                avalanchesearch_data_linearlayout.visibility = View.VISIBLE
            }
        }
        viewModel.firstClick = false
    }


    override fun onDestroy() {
        super.onDestroy()
        // Kan ikke trykke igjen før BottomSheet lukkes - hindrer krasj
        viewModel.elementOnClick = false
    }




    @SuppressLint("SetTextI18n")
    // Bruker regiontype dersom regionstype fra onClick er A (kommer egt som "Ikke gitt")
    private fun setData(data: List<AvalancheWarning>, regiontype: String?){
        coordinate_area_avalanchesearch_tv.text = "Område: ${data[0].RegionName}"
        if (regiontype != null) {
            regiontype_avalanchesearch_tv.text = "Regionstype: $regiontype"
        } else {
            regiontype_avalanchesearch_tv.text = "Regionstype: ${data[0].RegionTypeName}"
        }

        // Regner ut og viser avstanden mellom stedet og brukerens posisjon
        try {
            var currentItemCoordinates: LatLng? = null
            val addresses = if (viewModel.fromOnClick) {
                geocoder.getFromLocationName(data[0].RegionName, 1)
            } else {
                geocoder.getFromLocationName(viewModel.lastSearchPlace, 1)
            }
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

        val dateArray = data[0].ValidFrom!!.split('T')[0].split('-')
        date1_avalanchesearch_tv.text = "I dag - ${getDateString(dateArray)}"
        warning_day1_search_tv.setBackgroundColor(Color.parseColor(getColor(data[0].DangerLevel!!)))
        warning_day1_search_tv.text = data[0].DangerLevel!!
        maintext1_tv.text = data[0].MainText

        val dateArray2 = data[1].ValidFrom!!.split('T')[0].split('-')
        date2_avalanchesearch_tv.text = "I morgen - ${getDateString(dateArray2)}"
        warning_day2_search_tv.setBackgroundColor(Color.parseColor(getColor(data[1].DangerLevel!!)))
        warning_day2_search_tv.text = data[1].DangerLevel!!
        maintext2_tv.text = data[1].MainText

        val dateArray3 = data[2].ValidFrom!!.split('T')[0].split('-')
        date3_avalanchesearch_tv.text = "Om 2 dager - ${getDateString(dateArray3)}"
        warning_day3_search_tv.setBackgroundColor(Color.parseColor(getColor(data[2].DangerLevel!!)))
        warning_day3_search_tv.text = data[2].DangerLevel!!
        maintext3_tv.text = data[2].MainText
    }


    private fun getDateString(date: List<String>): String {
        val mnd = date[1]
        val day = date[2]
        val mndString = getMnd(mnd)
        return "$day. $mndString"
    }


    private fun getColor(i: String): String{
        return when(i.toIntOrNull()) {
            in 0..1 -> "#00ab2e"
            2 -> "#fff019"
            3 -> "#ff9f05"
            4 -> "#ff4f19"
            5 -> "#000000"
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



}