package com.example.in2000_team41.api.forestfire

data class ForestfireModel(val time: Time?, var locations: MutableList<LocationFireWarning>?)  // klassen brukes til parse av json-responsen


data class LocationFireWarning(val id: String?, val name: String?, val county: String?, val danger_index: String?)
data class Time(val to: String?, val from: String?)

data class ForestfireHolder(val today: LocationFireWarning?, val tomorrow: LocationFireWarning?, val twodays: LocationFireWarning?)