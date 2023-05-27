package com.unl.addressvalidator.model.reversegeocode

data class PostalAddressData(
    val country_code : String,
    val state_district : String,
    val city_district : String,
    val address_region : String,
    val street_address : String,
    val postal_code : String,
    val house_number : String

    )

