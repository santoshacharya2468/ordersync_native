package com.orderpush.app.features.category.di

import com.google.gson.Gson
import com.orderpush.app.features.category.data.api.CategoryApi
import com.orderpush.app.features.category.data.repository.CategoryRepositoryImpl
import com.orderpush.app.features.category.domain.repository.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object CategoryDi {
    @Provides
    @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi {
        return retrofit.create(CategoryApi::class.java)
    }


    @Provides
    @Singleton
    fun provideCategoryRepository(api: CategoryApi,gson: Gson): CategoryRepository{
        return CategoryRepositoryImpl(api,gson)
    }
}