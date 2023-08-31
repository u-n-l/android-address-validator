package com.unl.addressvalidator.model.address


import com.example.roomdatabasewithmodelclassess.model.*


data class AddressRequestModel(
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

