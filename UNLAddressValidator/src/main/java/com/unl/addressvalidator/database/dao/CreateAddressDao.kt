package com.unl.addressvalidator.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel

@Dao
interface CreateAddressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAddress(createAddressModel: CreateAddressModel)

    @Query("SELECT * FROM create_address")
    fun getAddressData(): LiveData<List<CreateAddressModel>>
}