package com.orderpush.app.features.customer.data.model

data class CustomerFilter(val limit: Int?,val page:Int?)
fun CustomerFilter.toMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    limit?.let { map["limit"] = it.toString() }
    page?.let { map["page"] = it.toString() }
    return map
}


