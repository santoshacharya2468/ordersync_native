package com.orderpush.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orderpush.app.features.kds.data.model.KdsSettings
import kotlinx.coroutines.flow.Flow


@Dao
interface KdsDao {
    @Query("SELECT * FROM KdsSettings LIMIT 1")
     fun getKdsSettings(): Flow<KdsSettings?>

     @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKdsSettings(settings: KdsSettings)


}