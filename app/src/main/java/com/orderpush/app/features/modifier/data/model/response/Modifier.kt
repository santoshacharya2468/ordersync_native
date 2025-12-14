package com.orderpush.app.features.modifier.data.model.response

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity
data class MenuModifier(
    @PrimaryKey val id:String,
    val storeId :String,
    val externalId:String?,
    val name:String,
    val description:String?,
    val status:String?,
    val minSelection:Int?,
    val maxSelection:Int?,
    val createdAt: Instant,
    val updatedAt:Instant,

    )

@Entity( foreignKeys = [
    ForeignKey(
        entity = MenuModifier::class,
        parentColumns = ["id"],
        childColumns = ["modifierId"],
        onDelete = ForeignKey.CASCADE
    )
],
    indices = [
        Index(value = ["modifierId"])
    ])
data class  MenuModifierOption(
    @PrimaryKey val id:String,
    val name:String,
    val price:Double,
    val modifierId:String,
    val status:String,
    val available:Int,
    val sold:Int,
    val createdAt: Instant,
    val updatedAt:Instant,
    )