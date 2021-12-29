package com.example.in2000_team41.api.metalerts

data class AlertInfo(val event: String?, val severity: String?, val urgency: String?, val certainty: String?, val effective: String?, val onset: String?, val expires: String?, val headline: String?, val description: String?, val instruction: String?, val parameter: Map<String, String>?, val area: Area?)
