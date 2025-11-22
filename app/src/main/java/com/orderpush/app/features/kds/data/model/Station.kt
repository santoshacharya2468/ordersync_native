package com.orderpush.app.features.kds.data.model

data class Station(
    val id:String,
    val name:String,
    val nextStationId:String?=null,
)
fun List<Station>.getStationByName(name:String): Station?{
    return this.firstOrNull{
        it.name.lowercase()==name.lowercase()
    }

}