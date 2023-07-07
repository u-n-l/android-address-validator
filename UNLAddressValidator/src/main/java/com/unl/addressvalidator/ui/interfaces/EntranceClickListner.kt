package com.unl.addressvalidator.ui.interfaces

import com.example.roomdatabasewithmodelclassess.model.EntranceModel
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse


interface EntranceClickListner {
    fun entranceEditClick(position: Int,entranceModel: ArrayList<EntranceModel>)
    fun entranceDeleteClick(position: Int,entranceModel: ArrayList<EntranceModel>)
    fun entranceImageClick(position: Int,entranceModel: ArrayList<EntranceModel>)
}