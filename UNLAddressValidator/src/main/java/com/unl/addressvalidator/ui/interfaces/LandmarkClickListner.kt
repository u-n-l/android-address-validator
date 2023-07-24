package com.unl.addressvalidator.ui.interfaces

import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse


interface LandmarkClickListner {
    fun landmarkItemClick(reverseGeoCodeResponse: LandmarkDataList)
    fun uploadLandmarkPic(position: Int,resulttList: ArrayList<LandmarkDataList>)
}