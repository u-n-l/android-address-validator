package com.unl.addressvalidator.model.reversegeocode

data class PropertiesData(val match_component : String,val category_name : String,val businessName : String, val place : PlaceData?, val postal_address : ArrayList<PostalAddressData>, val unl_location : UnlLocationData?,val contributor_info : ArrayList<ContributorInfoData>?)
