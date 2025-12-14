package com.orderpush.app.features.menuItem.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity
data class MenuItem(
    @PrimaryKey val id:String,
    val storeId :String,
    val externalItemId:String?,
    val name:String,
    val description:String?,
    val image:String?,
    val basePrice:Double,
    val isAvailable:Boolean=true,
    val preparationTime:Int?,
    val categoryId:String?,
    val createdAt: Instant,
    val updatedAt:Instant,

    )