package com.orderpush.app.core

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orderpush.app.features.customer.data.model.Customer
import com.orderpush.app.features.kds.data.model.KdsFontSize
import com.orderpush.app.features.kds.data.model.Station
import com.orderpush.app.features.order.data.model.DeliveryAddress
import com.orderpush.app.features.order.data.model.OrderItem
import com.orderpush.app.features.order.data.model.OrderMode
import com.orderpush.app.features.order.data.model.OrderStatus
import kotlinx.datetime.Instant

class OrderConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromDeliveryAddress(address: DeliveryAddress?): String? =
        address?.let { gson.toJson(it) }

    @TypeConverter
    fun toDeliveryAddress(json: String?): DeliveryAddress? =
        json?.let { gson.fromJson(it, DeliveryAddress::class.java) }



    @TypeConverter
    fun fromCustomer(customer: Customer?): String? =
        customer?.let { gson.toJson(it) }

    @TypeConverter
    fun toCustomer(json: String?): Customer? =
        json?.let { gson.fromJson(it, Customer::class.java) }

    @TypeConverter
    fun fromOrderItems(items: List<OrderItem>?): String? =
        items?.let { gson.toJson(it) }

    @TypeConverter
    fun toOrderItems(json: String?): List<OrderItem>? {
        if (json == null) return null
        val type = object : TypeToken<List<OrderItem>>() {}.type
        return gson.fromJson(json, type)
    }

    // Convert Instant to Long for Room
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }

    // Convert Long back to Instant
    @TypeConverter
    fun toInstant(epochMilli: Long?): Instant? {
        return epochMilli?.let { Instant.fromEpochMilliseconds(it) }
    }

    // Optional: store as ISO string
    @TypeConverter
    fun fromInstantToString(instant: Instant?): String? {
        return instant?.toString()
    }

    @TypeConverter
    fun fromStringToInstant(value: String?): Instant? {
        return value?.let { Instant.parse(it) }
    }

}
class EnumConverters {

    // OrderMode
    @TypeConverter
    fun fromOrderMode(value: OrderMode): String = value.name

    @TypeConverter
    fun toOrderMode(value: String?): OrderMode? = value?.let { OrderMode.valueOf(it) }


    // OrderStatus
    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = OrderStatus.valueOf(value)
}

class FontSizeConverter {
    @TypeConverter
    fun fromFontSize(fontSize: KdsFontSize): String = fontSize.name

    @TypeConverter
    fun toFontSize(name: String): KdsFontSize = KdsFontSize.valueOf(name)
}


class StationConverter{
    private val gson = Gson()
    @TypeConverter
    fun fromStation(station: Station?): String? =
        station?.let { gson.toJson(it) }

    @TypeConverter
    fun toStation(json: String?): Station? =
        json?.let { gson.fromJson(it, Station::class.java) }
}