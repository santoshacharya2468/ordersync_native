package com.orderpush.app.features.category.data.repository

import com.google.gson.Gson
import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.category.data.api.CategoryApi
import com.orderpush.app.features.category.data.model.request.CategoryFilter
import com.orderpush.app.features.category.data.model.response.Category
import com.orderpush.app.features.category.domain.repository.CategoryRepository
import jakarta.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val apiService: CategoryApi,
    private val gson: Gson
) : CategoryRepository {
    override suspend fun getCategory(filter: CategoryFilter): APIResponse<List<Category>> {
        return  apiService.getCategory().toApiResponse()
    }
}