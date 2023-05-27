package com.unl.addressvalidator.model.dbmodel


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomdatabasewithmodelclassess.model.*
import com.google.gson.JsonObject

@Entity(tableName = "create_address")
data class CreateAddressModel(
    @PrimaryKey(autoGenerate = true)
    val createAddressId: Long =0,
    val addressModel: AddressModel?= null,
    val addressType: String?=null,
    val locationModel: LocationModel?= null,
    val landmarkModel: LandmarkModel?= null,
    val entranceModel: ArrayList<EntranceModel>? =null,
    val openingHoursSpecificationModel: ArrayList<OpeningHoursSpecificationModel>?= null


)
