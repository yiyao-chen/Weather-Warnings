package com.example.in2000_team41.api.locationforecast

data class LocationForecastModel(val type: String?, val geometry: Geometry?, val properties: Properties)

data class Geometry(val type: String?, val coordinates: Array<Double>?)
data class Properties(val meta: Meta?, val timeseries: Array<TimeForecast>)
data class Meta(val updated_at: String?, val units: Units)
data class Units(val air_temperature: String?, val wind_speed: String?)
data class TimeForecast(val time: String?, val data: Data?)
data class Data(val instant: Instant?, val next_1_hours: Next1Hours?, val next_6_hours: Next6Hours?, val next_12_hours: Next12Hours?)
data class Instant(val details: Details)
data class Details(val air_temperature: String?, val wind_speed: String?, val wind_speed_of_gust: String?)
data class Next1Hours(val summary: Summary?, val details: NextHoursDetails)
data class Next6Hours(val summary: Summary?, val details: NextHoursDetails)
data class Next12Hours(val summary: Summary?, val details: NextHoursDetails)
data class Summary(val symbol_code: String?)
data class NextHoursDetails(val precipitation_amount_max: String?, val precipitation_amount_min: String?)
