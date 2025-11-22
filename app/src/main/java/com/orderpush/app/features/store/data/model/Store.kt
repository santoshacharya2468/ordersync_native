package com.orderpush.app.features.store.data.model

data class Store(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String?,
    val phone: String?,
    val email: String?,
    val address: String?,
    val logoUrl: String?,
    val status: String,
    val timezone: String,
    val createdAt: String,
)

