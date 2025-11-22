package com.orderpush.app.features.customer.data.model

data class Customer(
    val id: String,
    val externalCustomerId: String?,
    val storeId: String?,
    val phone: String,
    val email: String,
    val name: String,
    val createdAt: String,
)
