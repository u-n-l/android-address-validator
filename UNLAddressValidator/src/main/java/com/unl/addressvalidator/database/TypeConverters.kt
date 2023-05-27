
package com.unl.addressvalidator.database

import androidx.room.TypeConverter
import com.example.roomdatabasewithmodelclassess.model.AddressModel
import com.example.roomdatabasewithmodelclassess.model.EntranceModel
import com.example.roomdatabasewithmodelclassess.model.LandmarkModel
import com.example.roomdatabasewithmodelclassess.model.LocationModel
import com.example.roomdatabasewithmodelclassess.model.OpeningHoursSpecificationModel
import com.google.gson.Gson
@androidx.room.TypeConverters
class TypeConverters {
    private val gson = Gson()
    @TypeConverter
    fun fromAddressModel(addressModel: AddressModel?): String {
        return Gson().toJson(addressModel)
    }

    @TypeConverter
    fun toAddressModel(addressModelJson: String?): AddressModel {
        return Gson().fromJson(addressModelJson, AddressModel::class.java)
    }

    @TypeConverter
    fun fromLocationModel(locationModel: LocationModel?): String? {
        if(locationModel!= null)
        return Gson().toJson(locationModel)
        else
            return null
    }

    @TypeConverter
    fun toLocationModel(locationModelJson: String?): LocationModel? {
        if(locationModelJson!= null)
        return Gson().fromJson(locationModelJson, LocationModel::class.java)
        else
            return null
    }

    @TypeConverter
    fun fromLandmarkModel(landmarkModel: LandmarkModel?): String? {
        if(landmarkModel!= null)
        return Gson().toJson(landmarkModel)
        else
            return null
    }

    @TypeConverter
    fun toLandmarkModel(landmarkModelJson: String?): LandmarkModel? {
        if(landmarkModelJson!= null)
        return Gson().fromJson(landmarkModelJson, LandmarkModel::class.java)
        else
            return null
    }

    @TypeConverter
    fun fromEntranceModel(entranceModel: ArrayList<EntranceModel>?): String? {
        if(entranceModel!= null)
        return Gson().toJson(entranceModel)
        else
            return null
    }

    @TypeConverter
    fun toEntranceModel(entranceModelJson: String?): ArrayList<EntranceModel>? {
        if(entranceModelJson!= null)
        return Gson().fromJson(entranceModelJson, ArrayList<EntranceModel>()::class.java)
        else
            return null
    }

    @TypeConverter
    fun fromOpeningHoursSpecificationModel(openingHoursModel: ArrayList<OpeningHoursSpecificationModel>?): String? {
        if(openingHoursModel!= null)
        return Gson().toJson(openingHoursModel)
        else
            return null
    }

    @TypeConverter
    fun toOpeningHoursSpecificationModel(openingHoursModelJson: String?): ArrayList<OpeningHoursSpecificationModel>? {
        if(openingHoursModelJson!= null)
        return Gson().fromJson(openingHoursModelJson, ArrayList<OpeningHoursSpecificationModel>()::class.java)
        else
            return null
    }
}
