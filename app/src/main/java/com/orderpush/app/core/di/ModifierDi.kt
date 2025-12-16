package com.orderpush.app.core.di

import com.orderpush.app.features.modifier.data.api.ModifierApi
import com.orderpush.app.features.modifier.data.repository.ModifierRepositoryImpl
import com.orderpush.app.features.modifier.domain.repository.ModifierRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ModifierDi {
    @Provides
    @Singleton
    fun provideModifierApi(retrofit: Retrofit): ModifierApi {
        return retrofit.create(ModifierApi::class.java)
    }


    @Provides
    @Singleton
    fun provideModifierRepository(api: ModifierApi): ModifierRepository{
        return ModifierRepositoryImpl(api)
    }
}