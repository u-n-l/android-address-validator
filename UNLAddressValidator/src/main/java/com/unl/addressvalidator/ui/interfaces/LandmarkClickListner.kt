package com.unl.addressvalidator.ui.interfaces

import com.example.roomdatabasewithmodelclassess.model.LandmarkModel
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse


interface LandmarkClickListner {
    fun landmarkItemClick(reverseGeoCodeResponse: LandmarkDataList)
    fun uploadLandmarkPic(position: Int,resulttList: ArrayList<LandmarkDataList>)
    fun viewLandMarkPic(position: Int,resulttList: ArrayList<LandmarkModel>)
}