package com.unl.addressvalidator.model.landmark

import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse

data class LandmarkDataList(var url : String, var imgCount : String, val addressInfo : ReverseGeoCodeResponse,var imageList : ArrayList<String>)
