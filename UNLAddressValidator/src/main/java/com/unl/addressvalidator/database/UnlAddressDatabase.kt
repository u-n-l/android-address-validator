package com.unl.addressvalidator.database

import android.content.Context
import androidx.room.*

import com.unl.addressvalidator.database.dao.CreateAddressDao
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel

@Database(entities = [CreateAddressModel::class], version = 1, exportSchema = true)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class UnlAddressDatabase : RoomDatabase() {
    abstract fun createAddressDao(): CreateAddressDao

    companion object {
        @Volatile
        private var INSTANCE: UnlAddressDatabase? = null

        fun getInstance(context: Context): UnlAddressDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UnlAddressDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

