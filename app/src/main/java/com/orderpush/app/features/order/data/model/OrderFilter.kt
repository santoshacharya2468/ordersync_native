package com.orderpush.app.features.order.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

data class OrderFilter(val page:Int?=null,val limit:Int=10000,val mode: OrderMode?=null,
   val maxTotal: Double?=null,
    val statues: List<OrderStatus> = emptyList()    ,
    val fromDate: LocalDateTime?=null,
    val  toDate: LocalDateTime?=null,
    val station:String ? =null,
                       val query:String?=null,
    val updatedAt:Long= Clock.System.now().epochSeconds,
                       var lastSync:String?=null
    )

fun OrderFilter.toQueryMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    page?.let { map["page"] = it.toString() }
    limit.let { map["limit"] = it.toString() }
    mode?.let { map["mode"] = it.name }
    statues.forEach { map["status[]"] = it.name }
    maxTotal?.let { map["max_total"] = it.toString() }
    station?.let {
        map["station"] = it
    }
    lastSync?.let {
        map["last_sync"] = it
    }

    val todayRange = todayStartEnd()
    val from =this.fromDate?:todayRange.first
    val to =this.toDate?: todayRange.second
    map["fulfillment_from"] = from.toString()
    map["fulfillment_to"] =to.toString()
    map["order_by"] ="fulfillment_time"
    map["sort_dir"] ="desc"

    return map
}
fun OrderFilter.toDefaultFromDate(): Instant {
    if(this.fromDate!=null){
        val d=  this.fromDate.toInstant(TimeZone.currentSystemDefault())
        return  d
    }
    val map= this.toQueryMap()
    return Instant.parse(map["fulfillment_from"].toString())
}
fun OrderFilter.toDefaultToDate(): Instant {
    if(this.toDate!=null){
        val d=  this.toDate.toInstant(TimeZone.currentSystemDefault())
        return  d
    }
    val map= this.toQueryMap()
    val t= Instant.parse(map["fulfillment_to"].toString())
    return  t
}


fun  OrderFilter.appliedCount():Int{
    var count=0
    if(statues.isNotEmpty()){
        count++
    }
    if(mode!=null){
        count++
    }
    if(toDate!=null){
        count++

    }
    if(fromDate!=null){
        count++
    }
    if(maxTotal!=null){
        count++
    }

    return  count
}

fun todayStartEnd(): Pair<Instant, Instant> {
    val tz = TimeZone.currentSystemDefault()
    val now = Clock.System.now()
    val todayLocal = now.toLocalDateTime(tz).date
    val startOfDay = LocalDateTime(todayLocal.year, todayLocal.monthNumber, todayLocal.dayOfMonth, 0, 0)
    val endOfDay = LocalDateTime(todayLocal.year, todayLocal.monthNumber, todayLocal.dayOfMonth, 23, 59, 59)
    val startInstant = startOfDay.toInstant(tz)
    val endInstant = endOfDay.toInstant(tz)

    return startInstant to endInstant
}