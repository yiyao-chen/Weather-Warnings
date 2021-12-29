package com.example.in2000_team41.api.avalanche

data class AvalancheModel(val Id: Int?, val Name: String?, val TypeId: Int?,
                          val TypeName: String?, var AvalancheWarningList: List<AvalancheWarning>)
data class AvalancheWarning(val RegionName: String?, val RegionId: Int?, val RegionTypeName: String?,
                            val DangerLevel: String?, val ValidFrom: String?, val ValidTo: String?,
                            val NextWarningTime: String?, val PublishTime: String?, val MainText: String?)
