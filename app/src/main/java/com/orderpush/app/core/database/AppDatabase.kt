package com.orderpush.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.orderpush.app.core.EnumConverters
import com.orderpush.app.core.FontSizeConverter
import com.orderpush.app.core.OrderConverters
import com.orderpush.app.core.StationConverter
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.order.data.model.Order

@Database(entities = [KdsSettings::class, Order::class], version = 11, exportSchema = false)
@TypeConverters(OrderConverters::class, EnumConverters::class, FontSizeConverter::class,StationConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun kdsDao(): KdsDao

    suspend fun  clearAllData(){
        clearAllTables()
    }


}
