package com.orderpush.app.features.menuItem.di


import com.google.gson.Gson
import com.orderpush.app.features.menuItem.data.api.MenuItemApi
import com.orderpush.app.features.menuItem.data.repository.MenuItemRepositoryImpl
import com.orderpush.app.features.menuItem.domain.repository.MenuItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object MenuItemDi {
    @Provides
    @Singleton
    fun provideMenuItemApi(retrofit: Retrofit): MenuItemApi {
        return retrofit.create(MenuItemApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMenuItemRepository(api: MenuItemApi,gson: Gson): MenuItemRepository{
        return MenuItemRepositoryImpl(api)
    }
}