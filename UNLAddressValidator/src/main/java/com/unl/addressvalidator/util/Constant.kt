package com.unl.addressvalidator.util

object Constant {

    const val META_VPM_ID_KEY = "com.unl.global.vpmid"
    const val META_API_KEY = "com.unl.global.apikey"

    const val ADD_ADDRESS = "Add Address"
    const val EDIT_ADDRESS = "Edit Address"

    const val ELEVATOR_ACCESSIBILITY = "Elevator"
    const val WHEELCHAIR_ACCESSIBILITY = "Wheelchair"

    const val BASE_URL_ALPHA = "https://alpha.api.unl.global/v2/"
    const val BASE_URL_SANDBOX = "https://sandbox.api.unl.global/v2/"
    const val BASE_URL = "https://api.unl.global/v2/"
    const val IMAGE_UPLOAD_BASE_URL = "https://alpha.api.unl.global/v1/"

    const val CAMERA_REQUEST_CODE = 102
    const val CAMERA_PERM_CODE = 101

    const val FROM_HOURS = "fromHours"
    const val FROM_MINS = "fromMins"
    const val TO_HOURS = "toHours"
    const val TO_MINS = "toMins"

    const val ALL_DAYS = "All Day's"
    const val MONDAY = "Mon"
    const val TUESDAY = "Tue"
    const val WEDNESDAY = "Wed"
    const val THURSDAY = "Thur"
    const val FRIDAY = "Fri"
    const val SATURDAY = "Sat"
    const val SUNDAY = "Sun"

    const val OPEN = "open"
    const val CLOSE = "close"

    const val ALLDAY = 0
    const val MON = 1
    const val TUES = 2
    const val WED = 3
    const val THU = 4
    const val FRI = 5
    const val SAT = 6
    const val SUN = 7


    const val KEY_ADDRESS = "name"
    const val KEY_ADDRESS_NUMBER = "address.houseNumber"
    const val KEY_ADDRESS_FLOOR = "address.floor"
    const val KEY_ADDRESS_NAME = "address.buildingName"
    const val KEY_ADDRESS_STREET = "address.street"
    const val KEY_ADDRESS_COLONY_NAME = "address.colonyName"
    const val KEY_ADDRESS_CITY = "address.city"
    const val KEY_ADDRESS_STATE = "address.state"
    const val KEY_ADDRESS_COUNTRY = "address.country"
    const val KEY_ADDRESS_POSTALCODE = "address.postalCode"
    const val KEY_ADDRESS_LATITUDE = "location.latitude"
    const val KEY_ADDRESS_LONGITUDE = "location.longitude"
    const val KEY_ADDRESS_TYPE = "addressType"
/*    const val KEY_ADDRESS_LANDMARK_NAME = "landmark.landmark_name"
    const val KEY_ADDRESS_LANDMARK_TYPE = "landmark._type"
    const val KEY_ADDRESS_LANDMARK_LATITUDE = "landmark.location.latitude"
    const val KEY_ADDRESS_LANDMARK_LONGITUDE = "landmark.location.longitude"*/
    const val KEY_ADDRESS_BUILDING_NUMBER = "address.buildingNumber"
    const val KEY_ADDRESS_NEIGHBOURHOOD = "address.neighbourhood"

 /*   const val KEY_ADDRESS_OPEN_0 = "openingHoursSpecification[0].opens"
    const val KEY_ADDRESS_CLOSE_0 = "openingHoursSpecification[0].closes"
    const val KEY_ADDRESS_DAY_0 = "openingHoursSpecification[0].day_of_week"
    const val KEY_ADDRESS_IS_HOLIDAY_0 = "openingHoursSpecification[0].is_holiday"

        .addFormDataPart("entrance[0].entrance_no", "1")
        .addFormDataPart("entrance[0].entrance_name", "main gate")
        .addFormDataPart("entrance[1].entrance_no", "2")
        .addFormDataPart("entrance[1].entrance_name", "side gate")*/


}