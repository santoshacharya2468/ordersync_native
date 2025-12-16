package com.orderpush.app.core.di

import KotlinxInstantAdapter
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orderpush.app.core.database.AppDatabase
import com.orderpush.app.core.database.KdsDao
import com.orderpush.app.core.database.OrderDao
import com.orderpush.app.core.network.AuthInterceptor
import com.orderpush.app.core.network.AuthInterceptor.TokenAuthenticator
import com.orderpush.app.core.network.ConnectivityObserver
import com.orderpush.app.core.network.NetworkConfiguration
import com.orderpush.app.core.services.SocketManager
import com.orderpush.app.core.session.SessionEventBus
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.auth.data.api.AuthApi
import com.orderpush.app.features.auth.data.repository.AuthRepositoryImpl
import com.orderpush.app.features.auth.domain.repository.AuthRepository
import com.orderpush.app.features.customer.data.api.CustomerApi
import com.orderpush.app.features.customer.data.repository.CustomerRepositoryImpl
import com.orderpush.app.features.customer.domain.repository.CustomerRepository
import com.orderpush.app.features.kds.data.api.StationApi
import com.orderpush.app.features.kds.data.repository.KdsRepositoryImpl
import com.orderpush.app.features.kds.domain.repository.KdsRepository
import com.orderpush.app.features.order.data.api.OrderApi
import com.orderpush.app.features.order.data.repository.OrderRepositoryImpl
import com.orderpush.app.features.order.domain.repository.OrderRepository
import com.orderpush.app.features.store.data.api.StoreApi
import com.orderpush.app.features.store.data.repository.StoreRepositoryImpl
import com.orderpush.app.features.store.domain.repository.StoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.Instant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object  DiAppModule {

    @Provides
    fun provideConnectivityManager(@ApplicationContext appContext: Context): ConnectivityObserver{
       return  ConnectivityObserver(appContext)
    }

    @Provides
    @Singleton
    fun provideSessionEventBus(): SessionEventBus = SessionEventBus()
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, KotlinxInstantAdapter())
        .create()


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "order_push.db")
            .fallbackToDestructiveMigration(true)
            .build()
    @Provides
    @Singleton
    fun provideOrderDao(database: AppDatabase) = database.orderDao()


    @Provides
    @Singleton
    fun provideKdsDao(database: AppDatabase) = database.kdsDao()
    @Provides
    @Singleton
    fun provideNetworkConf(): NetworkConfiguration{
        return NetworkConfiguration(baseUrl = "https://orderpushapi.amigolive.net/", socketUrl = "https://orderpush-socket.amigolive.net")
    }
    @Provides
    @Singleton
    fun provideSessionManager(
        prefs: SharedPreferences,
        gson: Gson,
        database: AppDatabase
    ): SessionManager {
        return SessionManager(prefs,gson,database)
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(networkConfiguration: NetworkConfiguration): AuthApi {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl(networkConfiguration.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)

    }



        @Provides
        fun provideRetrofit(
            sessionManager: SessionManager, networkConf: NetworkConfiguration,
            gson: Gson, sessionEventBus: SessionEventBus,
            @Named("auth") authApi: AuthApi
        ): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .authenticator(TokenAuthenticator(sessionManager, sessionEventBus, authApi))
                .addInterceptor(
                    AuthInterceptor(
                        sessionManager,

                    )
                ) // attach your interceptor
                .build()

            return Retrofit.Builder()
                .baseUrl(networkConf.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        }

        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }

        @Provides
        @Singleton
        fun provideStationApi(retrofit: Retrofit): StationApi {
            return retrofit.create(StationApi::class.java)
        }

        @Provides
        @Singleton
        fun provideOrderApi(retrofit: Retrofit): OrderApi {
            return retrofit.create(OrderApi::class.java)
        }

        @Provides
        @Singleton
        fun provideStoreApi(retrofit: Retrofit): StoreApi {
            return retrofit.create(StoreApi::class.java)
        }

        @Provides
        @Singleton
        fun provideCustomerApi(retrofit: Retrofit): CustomerApi {
            return retrofit.create(CustomerApi::class.java)
        }

        @Provides
        @Singleton
        fun provideAuthRepository(
            api: AuthApi,
            sessionManager: SessionManager,
            @ApplicationContext context: Context
        ): AuthRepository {
            return AuthRepositoryImpl(api, context, sessionManager)
        }


        @Provides
        @Singleton
        fun provideOrderRepository(
            api: OrderApi, sessionManager: SessionManager,
            socketManager: SocketManager,
            networkConf: NetworkConfiguration,
            orderDao: OrderDao,
            @ApplicationContext context: Context,
            gson: Gson
        ): OrderRepository {
            return OrderRepositoryImpl(
                api,
                sessionManager,
                socketManager,
                networkConf,
                orderDao = orderDao,
                gson = gson,
                context = context
            )
        }

        @Provides
        @Singleton
        fun provideStoreRepository(api: StoreApi): StoreRepository {
            return StoreRepositoryImpl(api)
        }

        @Provides
        @Singleton
        fun provideCustomerRepository(
            api: CustomerApi,
            sessionManager: SessionManager
        ): CustomerRepository {
            return CustomerRepositoryImpl(api, sessionManager)
        }


        @Provides
        @Singleton
        fun provideKdsRepository(
            dao: KdsDao,
            sessionManager: SessionManager,
            stationApi: StationApi
        ): KdsRepository {
            return KdsRepositoryImpl(dao, stationApi, sessionManager)
        }


        @Provides
        @Singleton
        fun provideEncryptedPrefs(@ApplicationContext context: Context): SharedPreferences {
            return EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }


    }


