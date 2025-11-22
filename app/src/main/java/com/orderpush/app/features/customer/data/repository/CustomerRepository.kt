package com.orderpush.app.features.customer.data.repository

import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.customer.data.api.CustomerApi
import com.orderpush.app.features.customer.data.model.Customer
import com.orderpush.app.features.customer.data.model.CustomerFilter
import com.orderpush.app.features.customer.data.model.toMap
import com.orderpush.app.features.customer.domain.repository.CustomerRepository
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(private val api: CustomerApi,
   val  sessionManager: SessionManager
    ) : CustomerRepository {
    override suspend fun getCustomers(filter: CustomerFilter): APIResponse<List<Customer>> {

     return  api.getCustomers(sessionManager.getStore()?: "",filter.toMap()).toApiResponse()
    }

}