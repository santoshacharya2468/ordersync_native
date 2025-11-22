package com.orderpush.app.features.kds.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class KdsFontSize(val size:Int) {
    Small(14),
    Medium(16),
    Large(18),
    ExtraLarge(20)
}

enum class OrderDisplayMode{
    Tiled,Classic
}


@Entity
data class KdsSettings(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val selectedKdsStation: String? = null,
    val fontSize: KdsFontSize = KdsFontSize.Medium,
    // Status Colors
    val onTimeOrderColor: String = "#4CAF50",
    val warningOrderColor: String = "#FFA726",
    val lateOrderColor: String = "#EF5350",

    // Order Type Colors
    val forHereColor: String = "#4CAF50",
    val driveThruColor: String = "#BA68C8",
    val curbsideColor: String = "#4DD0E1",
    val toGoColor: String = "#FFA726",
    val deliveryColor: String = "#AB47BC",
    val pickupColor: String = "#4DD0E1",

    // Text Colors
    val mainTextColor: String = "#ffffff",
    val modifierTextColor: String = "#1565C0",
    //sound
    val newOrderNotification: Boolean = true,
    val soundVolume: Int = 100,

    //display modes
    val displayMode: OrderDisplayMode= OrderDisplayMode.Tiled,

    //station filter settings
    val station: Station?=null
)
