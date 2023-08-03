package com.unl.addressvalidator.ui.fragments


import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import com.example.roomdatabasewithmodelclassess.model.*
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.geometry.LatLng
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.util.Utility
import org.json.JSONArray
import org.json.JSONObject


fun HomeFragment.setMapPointAddress() {
    var houseNumber =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).house_number
    var streetAddress =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).street_address
    var city =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).city_district
    var pincode =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).postal_code
    var state =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).state_district
    var countryCode =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).country_code

    binding!!.addNewAdd!!.edtUnit.setText(houseNumber)
    binding!!.addNewAdd!!.edtStreet.setText(streetAddress)
    binding!!.addNewAdd!!.edtCity.setText(city)
    binding!!.addNewAdd!!.edtPincode.setText(pincode )
    binding!!.addNewAdd!!.edtState.setText(state)
    binding!!.addNewAdd!!.edtCountry.setText(countryCode)

    binding!!.addNewAdd.addressView.visibility = View.VISIBLE
    binding!!.addNewAdd.tvAddressText.text  = Utility.returnFullAddress(houseNumber,"","",streetAddress,city,state,pincode)

}



fun HomeFragment.setAddressObject() {

    var houseNo =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).house_number
    var streetName =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).street_address
    var city =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).city_district
    var pincode =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).postal_code
    var state =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).state_district
    var country =  reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).country_code

    var address  = Utility.returnFullAddress(houseNo,"","",streetName,city,state,pincode)
    binding!!.addNewAdd.tvAddressText.text  = address

    var floor = ""
    var buildingNum = ""
    var buildingName = ""

    var neighbour = ""

    if (addressType.equals("other")) {
        addressType = binding!!.addNewAdd!!.edtLabelName.text!!.toString()
    }
    val addressModel = AddressModel(
        houseNo,
        floor,
        buildingNum,
        buildingName,
        streetName,
        neighbour,
        cityText,
        stateText,
        country,
        pincodeText
    )

    val locationModel = LocationModel(pinLat!!, pinLong!!)
    var landmarkModelList = ArrayList<LandmarkModel>()
    val landmarkModel = LandmarkModel(
        "",
        "",
        "00.00",
        "00.00",
        "",
        ArrayList<String>()
    )
    landmarkModelList.add(landmarkModel)
    var entranceList: ArrayList<EntranceModel> = ArrayList()


    var openCloseTimeList: ArrayList<OpeningHoursSpecificationModel> = ArrayList()
    val openingHoursSpecificationModel =
        OpeningHoursSpecificationModel("11:00:00", "Monday", false, "08:00:00")
    if (dataList != null && dataList.size > 0) {
        imageList.clear()
        dataList.forEach() {
            var str : String = it.ivPhotos.toString()

            if (str!= null && !str.equals(""))
                imageList.add(str)
        }
    } else {
        imageList.clear()
    }

    openCloseTimeList.add(openingHoursSpecificationModel)
    createAddressModel = CreateAddressModel(
        addressModel,
        addressType,
        address,
        locationModel,
        landmarkModelList,
        entranceList,
        imageList,
        ArrayList<String>(),
        openCloseTimeList
    )
}



 fun HomeFragment.showReverseGeoAddress() {
    try {
        binding!!.confirmAddress!!.root.visibility = View.VISIBLE
        binding!!.addNewAdd!!.root.visibility = View.GONE

        binding!!.confirmAddress!!.tvAddressText!!.text = reverseGeoCodeResponse!!.features!!.get(0).properties!!.place!!.name
        binding!!.confirmAddress!!.tvConfirm.setOnClickListener {
            binding!!.confirmAddress!!.root.visibility = View.GONE
            openLandmarkPopup()
            setAddressObject()
        }
        binding!!.confirmAddress!!.mainLayout.setOnClickListener {
        }
        binding!!.confirmAddress!!.addNew.setOnClickListener {
            binding!!.confirmAddress!!.root.visibility = View.GONE
            binding!!.addNewAdd!!.root.visibility = View.VISIBLE
        }
        binding!!.confirmAddress!!.editAddress.setOnClickListener {
            binding!!.confirmAddress!!.root.visibility = View.GONE
            binding!!.addNewAdd!!.root.visibility = View.VISIBLE
            setMapPointAddress()
            setAddressObject()
        }
        binding!!.confirmAddress!!.addImage.setOnClickListener {
            selectImageForEntrance = false
            selectImageForLandmakr = false
            showAddPictureDialog()
        }


    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}
