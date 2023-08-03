package com.unl.addressvalidator.model.dbmodel


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomdatabasewithmodelclassess.model.*
import com.google.gson.JsonObject

@Entity(tableName = "create_address")
data class CreateAddressModel(
    var addressModel: AddressModel?= null,
    var addressType: String?=null,
    var address: String?=null,
    var locationModel: LocationModel?= null,
    var landmarkModel: ArrayList<LandmarkModel>?= null,
    var entranceModel: ArrayList<EntranceModel>? =null,
    var images: ArrayList<String>? =null,
    var accessibility: ArrayList<String>? =null,
    var openingHoursSpecificationModel: ArrayList<OpeningHoursSpecificationModel>?= null
)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

