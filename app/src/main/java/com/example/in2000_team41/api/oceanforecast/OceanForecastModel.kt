package com.example.in2000_team41.api.oceanforecast

data class OceanForecastModel(val type: String?, val geometry: Geometry?, val properties: Properties)

data class Geometry(val type: String?, val coordinates: List<Double>?)
data class Properties(val meta: Meta?, val timeseries: List<TimeForecast>)
data class Meta(val updated_at: String?)
data class TimeForecast(val time: String?, val data: Data?)
data class Data(val instant: Instant?)
data class Instant(val details: Details)
data class Details(val sea_water_temperature: Double?, val sea_water_speed: Double?, val sea_surface_wave_height: Double?)
