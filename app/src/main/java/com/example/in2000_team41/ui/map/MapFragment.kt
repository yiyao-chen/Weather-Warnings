package com.example.in2000_team41.ui.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.in2000_team41.R
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.ui.home.HomeViewModel
import com.example.in2000_team41.utilities.LocationHelper
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private val viewModelHome: HomeViewModel by activityViewModels()
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //setter header
        requireActivity().title = resources.getString(R.string.kart)
        //for autocomplete-soek
        val apiKey = getString(R.string.autocomplete_api_key)
        //activity stuff (?)
        val activity = activity as Context
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey)
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
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
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}")
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 10f))
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })
        return view
    }


    // Callback fra Android som kjøres når man får et svar om en bruker har godtatt/avslått tilgang til posisjon.
    // Når dette skjer oppdaterer vi kartets posisjon, i tilfelle brukeren ga appen tilgang til posisjon.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        LocationHelper.instance.handleRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        updateMap()
    }

    // Funksjon som flytter kameraet på kartet til brukerens posisjon og skrur av/på blå markør av brukerens posisjon
    private fun updateMap() {
        LocationHelper.instance.getDeviceLocation(requireActivity()) {
            try {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 6f))
                map.isMyLocationEnabled = LocationHelper.instance.locationPermissionGranted
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        // Trenger ikke rotasjon på kart
        map.uiSettings.isRotateGesturesEnabled = false

        // Ber om tilgang til brukerens posisjon og oppdaterer kartet, når Google Maps er klart
        LocationHelper.instance.getLocationAccess(requireContext(), this)
        updateMap()

        // henter og setter opp Polygoner på kartet
        getAreas()
    }


    private fun getAreas() {
        //Hente data fra MetAlerts
        viewModel.setMetAlertsApiData()
        viewModel.metAlertsLiveData()!!.observe(viewLifecycleOwner, { dataList ->
            viewModel.updateMetAlertList(dataList)

            //Hente detaljert info om MetAlerts
            viewModel.setFullAlertsApiData(dataList, viewModelHome.coordinates)
            viewModel.fullAlertsLiveData()!!.observe(viewLifecycleOwner, { alerts ->
                //Legge til data fra alert i liste i ViewModel
                viewModel.updateAlertList(alerts)

                //Finne area i hver alert overfor, og lage polygon
                for (alert in viewModel.listAlertModel) {
                    makePolygon(alert)
                    setMarker(alert)

                    // adding on click listener to marker of google maps.
                    map.setOnMarkerClickListener { marker ->
                        marker.hideInfoWindow();
                        val builder = AlertDialog.Builder(activity)
                        val base = viewModel.listAlertModel[marker.title.toInt()]
                        builder.setTitle(base.info!!.event)
                        builder.setMessage(
                            base.info!!.area!!.areaDesc
                                    + "\n\n${
                                String.format(
                                    "%.2f",
                                    base.distance
                                ) + "km fra " + viewModelHome.userAdress
                            }"
                        )
                        builder.setPositiveButton("Les mer") { _, _ ->
                            viewModel.setAlertDataMap(marker.title.toInt())
                            findNavController().navigate(R.id.action_navigation_map_to_alertDetailFragmentMap)
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                        true
                    }
                }
            })
        })
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun makePolygon(alert: AlertModel) {
        val coordinates = alert.info!!.area!!.polygon!!.split(" ")
        //Tegner polygoner
        val polyOptions = PolygonOptions()
        for (set in coordinates) {
            val result0 = set.filter { it.isDigit() || it == '.' || it == ',' }
            val coordinatePair = result0.split(",")
            if (coordinatePair[0].isNotEmpty() && coordinatePair[1].isNotEmpty()) {
                polyOptions.add(LatLng(coordinatePair[0].toDouble(), coordinatePair[1].toDouble()))
                polyOptions.fillColor(0x7Fdd9cc9)
                polyOptions.strokeColor(Color.RED)
                polyOptions.strokeWidth(3.0F)
            }
        }

        val polygon = map.addPolygon(
            (polyOptions)
                .clickable(true)
        )

        onClickPolygon(polygon, alert)
    }

    private fun onClickPolygon(polygon: Polygon, alert: AlertModel) {
        // oppretter AlertDialog
        val builder = AlertDialog.Builder(activity)
        polygon.tag = viewModel.listAlertModel.indexOf(alert) // finner indeksen til alert-objekt
        map.setOnPolygonClickListener {
            val base = viewModel.listAlertModel[it.tag as Int]
            builder.setTitle(base.info!!.event)
            builder.setMessage(
                base.info!!.area!!.areaDesc
                        + "\n\n${
                    String.format(
                        "%.2f",
                        base.distance
                    ) + "km fra " + viewModelHome.userAdress
                }"
            )
            builder.setPositiveButton("Les mer") { _, _ ->
                viewModel.setAlertDataMap(it.tag as Int)
                findNavController().navigate(R.id.action_navigation_map_to_alertDetailFragmentMap)
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }


    private fun setMarker(alert: AlertModel) {
        val coordinates = alert.info!!.area!!.polygon!!.split(" ")
        // Størrelsen til iconene
        val height = 200
        val width = 150
        // henter riktig drawable til markøren
        val img = getMarkerImage(alert.info!!.event!!)
        val b = img.bitmap
        val smallMarker = createScaledBitmap(b, width, height, false)
        // setter icoene på kortet
        val positionMarker = getCentralCoordinate(coordinates)
        map.apply {
            addMarker(
                MarkerOptions()
                    .position(positionMarker)
                    .title(viewModel.listAlertModel.indexOf(alert).toString())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            )
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getMarkerImage(event: String): BitmapDrawable {
        return when (event.toLowerCase(Locale.ROOT)) {
            "kuling" -> resources.getDrawable(R.drawable.map_marker_wind) as BitmapDrawable
            "storm" -> resources.getDrawable(R.drawable.map_marker_storm) as BitmapDrawable
            "sterk ising på skip" -> resources.getDrawable(R.drawable.map_marker_cold) as BitmapDrawable
            "snø" -> resources.getDrawable(R.drawable.map_marker_snow) as BitmapDrawable
            "kraftig snøfokk" -> resources.getDrawable(R.drawable.map_marker_snow) as BitmapDrawable
            "skogbrannfare" -> resources.getDrawable(R.drawable.map_marker_forestfire) as BitmapDrawable
            else -> resources.getDrawable(R.drawable.map_marker_warning) as BitmapDrawable
        }
    }

    private fun getCentralCoordinate(coordinatesPolygon: List<String>): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        for (set in coordinatesPolygon) {
            val result0 = set.filter { it.isDigit() || it == '.' || it == ',' }
            val coordinatePair = result0.split(",")
            if (coordinatePair[0].isNotEmpty() && coordinatePair[1].isNotEmpty()) {
                latitude = coordinatePair[0].toDouble()
                longitude = coordinatePair[1].toDouble()
            }
        }
        return LatLng(latitude, longitude)
    }

}
