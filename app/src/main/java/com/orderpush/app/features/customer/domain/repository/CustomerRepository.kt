package com.orderpush.app.features.customer.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.customer.data.model.Customer
import com.orderpush.app.features.customer.data.model.CustomerFilter

interface CustomerRepository {
    suspend fun  getCustomers(filter: CustomerFilter): APIResponse<List<Customer>>
}