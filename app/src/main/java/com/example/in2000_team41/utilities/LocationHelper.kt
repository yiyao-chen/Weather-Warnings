package com.example.in2000_team41.utilities

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

// Hjelpe-klasse som lar flere deler av appen få tilgang til brukerens posisjon
class LocationHelper {

    // Ved å bruke et companion object vil alle klasser på appen få tilgang til samme instans
    companion object {
        val instance = LocationHelper()
    }

    // Location request ID
    private val LOCATION_PERMISSION_REQUEST = 1

    //lager en variabel for koordinater som i starten blir satt til oslo
    var coords = LatLng(59.911491, 10.757933)

    // Variabel som forteller om tilgang til posisjon er tillatt
    var locationPermissionGranted = false


    // Sjekker om tilgang er gitt og forespør eventuelt om man ikke har gitt tilgang tidligere
    fun getLocationAccess(context: Context, fragment: Fragment){
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            // Ber om tilgang dersom man ikke har det
            fragment.requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }
    }

    // Hjelpefunksjon som kan brukes for å hente Latitude og Longitude til brukeren, dersom man har tilgang til posisjon
    fun getDeviceLocation(activity: Activity, result: (location: LatLng) -> Unit) {
        try {
            if (locationPermissionGranted) {
                val locationResult = LocationServices.getFusedLocationProviderClient(activity).lastLocation
                locationResult.addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful && task.result != null) {
                        coords = LatLng(task.result!!.latitude, task.result!!.longitude)

                        // Returnerer brukerens koordinater
                        result(LatLng(task.result!!.latitude, task.result!!.longitude))
                    } else {
                        // Ved en feil returneres Oslo sine koordinater
                        val oslo = LatLng(59.911491, 10.757933)
                        result(oslo)
                    }
                }
            } else {
                // Ved ingen tilgang returneres Oslo sine koordinater
                val oslo = LatLng(59.911491, 10.757933)
                result(oslo)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }



    // Denne funksjonen må kalles fra Fragmentet som ber om tilgang til posisjon, for å oppdatere om man har tilgang
    fun handleRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }
}