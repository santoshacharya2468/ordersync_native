package com.orderpush.app.features.category.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.category.data.model.request.CategoryFilter
import com.orderpush.app.features.category.data.model.response.Category

interface CategoryRepository {
    suspend fun getCategory(filter: CategoryFilter): APIResponse<List<Category>>
}