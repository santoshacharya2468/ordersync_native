package com.orderpush.app.features.category.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity
data class Category(
    @PrimaryKey val id:String,
    val storeId :String,
    val externalId:String?,
    val name:String,
    val description:String?,
    val image:String?,
    val status:String?,
    val createdAt: Instant,
    val updatedAt:Instant,
    )