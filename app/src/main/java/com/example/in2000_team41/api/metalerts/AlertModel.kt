package com.example.in2000_team41.api.metalerts

data class AlertModel(var distance: Double?, val id: String?, val sender: String?, val sent: String?, val status: String?, val msgType: String?, val scope: String?, var info: AlertInfo?)
