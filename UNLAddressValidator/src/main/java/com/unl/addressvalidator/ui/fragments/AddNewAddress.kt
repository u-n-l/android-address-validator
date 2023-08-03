package com.unl.addressvalidator.ui.fragments


import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.roomdatabasewithmodelclassess.model.*
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.geometry.LatLng
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.util.Utility
import org.json.JSONArray
import org.json.JSONObject

fun HomeFragment.nodatafund()
{

}

fun HomeFragment.setAddressFromCurrentLcation() {

    var houseNumber =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties!!.postal_address.get(0).house_number
    var streetAddress =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties!!.postal_address.get(0).street_address
    var city =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties!!.postal_address.get(0).city_district
    var pincode =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties!!.postal_address.get(0).postal_code
    var state =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties!!.postal_address.get(0).state_district
    var countryCode =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties!!.postal_address.get(0).country_code

    binding!!.addNewAdd!!.edtUnit.setText(houseNumber)
    binding!!.addNewAdd!!.edtStreet.setText(streetAddress)
    binding!!.addNewAdd!!.edtCity.setText(city)
    binding!!.addNewAdd!!.edtPincode.setText(pincode )
    binding!!.addNewAdd!!.edtState.setText(state)
    binding!!.addNewAdd!!.edtCountry.setText(countryCode)

    binding!!.addNewAdd.addressView.visibility = View.VISIBLE
    binding!!.addNewAdd.tvAddressText.text  = Utility.returnFullAddress(houseNumber,"","",streetAddress,city,state,pincode)


}

fun HomeFragment.updateAddressButton() {

    if (cityText.isNotEmpty() && stateText.isNotEmpty() && pincodeText.isNotEmpty()) {
        binding!!.addNewAdd!!.tvConfirm.setBackgroundResource(R.drawable.theme_round_btn)
        binding!!.addNewAdd!!.tvConfirm.isClickable = true
    } else {
        binding!!.addNewAdd!!.tvConfirm.setBackgroundResource(R.drawable.bg_button_disable)
        binding!!.addNewAdd!!.tvConfirm.isClickable = false
    }

}

fun HomeFragment.setNewAddressClick() {
    binding!!.addNewAdd!!.tvConfirm.setOnClickListener {
        binding!!.addNewAdd!!.root.visibility = View.GONE
        binding!!.confirmAddress!!.root.visibility = View.GONE
        createAddress()
        openLandmarkPopup()

    }
    binding!!.addNewAdd!!.mainLayout.setOnClickListener {

    }

     binding!!.addNewAdd!!.addImage.setOnClickListener {
         selectImageForEntrance = false
         selectImageForLandmakr = false
            showAddPictureDialog()
    }


     binding!!.addNewAdd!!.deleteAddress.setOnClickListener {
            clearAddressFields()
         binding!!.addNewAdd!!.addressView.visibility = View.GONE
    }


    binding!!.addNewAdd!!.currentAddressLayout.setOnClickListener {
        clearMap()
        clearAddressFields()
        setAddressFromCurrentLcation()
        showMarker(LatLng(currLat, currLong), "Current Location")
        changeCameraPosition(LatLng(currLat, currLong))
    }

    binding!!.addNewAdd.rlHomeIcon.setOnClickListener(View.OnClickListener { selectedLabel(0) })
    binding!!.addNewAdd.rlOfficeIcon.setOnClickListener(View.OnClickListener { selectedLabel(1) })
    binding!!.addNewAdd.rlAddLableIcon.setOnClickListener(View.OnClickListener { selectedLabel(2) })
    binding!!.addNewAdd!!.edtCity.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            try {
                if (s!!.isNotEmpty() && s!!.toString().length > 1) {
                    cityText = s.toString()
                } else
                    cityText = ""

                updateAddressButton()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?, start: Int,
            before: Int, count: Int
        ) {


        }
    })

    binding!!.addNewAdd!!.edtState.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            try {
                if (s!!.isNotEmpty() && s!!.toString().length > 1) {
                    stateText = s.toString()
                } else
                    stateText = ""
                updateAddressButton()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?, start: Int,
            before: Int, count: Int
        ) {


        }
    })

    binding!!.addNewAdd!!.edtPincode.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            try {
                if (s!!.isNotEmpty() && s!!.toString().length > 1) {
                    pincodeText = s.toString()
                } else
                    pincodeText = ""

                updateAddressButton()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?, start: Int,
            before: Int, count: Int
        ) {


        }
    })
}

fun HomeFragment.createAddress() {

    var houseNo = binding!!.addNewAdd!!.edtUnit.text!!.toString()
    var floor = binding!!.addNewAdd!!.edtFloor.text!!.toString()
    var buildingNum = binding!!.addNewAdd!!.edtBuildingNum.text!!.toString()
    var buildingName = binding!!.addNewAdd!!.edtBuildingName.text!!.toString()
    var streetName = binding!!.addNewAdd!!.edtStreet.text!!.toString()
    var neighbour = binding!!.addNewAdd!!.edtNeighbour.text!!.toString()
    var country = binding!!.addNewAdd!!.edtCountry.text!!.toString()


    var address  = Utility.returnFullAddress(houseNo,floor,buildingName,streetName,cityText,stateText,pincodeText)

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
        "Gyan Sagar school",
        "schood",
        "22.56765434",
        "75.324565432",
        "https://www.image.jpeg",
        ArrayList<String>()
    )
    landmarkModelList.add(landmarkModel)



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
   // viewModel.insertAddress(database, createAddress)
    //getAddressCreated()
}

fun HomeFragment.parseSearchResultJson(rawData: JsonObject) {

    try {
        val parsed = JSONObject(rawData.toString())
        val featuresArray: JSONArray = parsed.getJSONArray("geojson:Features")
        var featureArrayJson: String = featuresArray.getString(0)
        val featureJson: JSONObject = JSONObject(featureArrayJson)
        var propertiesData: JSONObject = featureJson.getJSONObject("geojson:properties")
        val addressArray: JSONArray = propertiesData.getJSONArray("vocabulary:address")
        var AddressObject: String = addressArray.getString(0)
        val AddressJson: JSONObject = JSONObject(AddressObject)
        var addressType: String = AddressJson.getString("@type")
        var addressRegion: String = AddressJson.getString("vocabulary:addressRegion")
        var cityDistrict: String = AddressJson.getString("vocabulary:cityDistrict")
        var houseNumber: String = AddressJson.getString("vocabulary:houseNumber")
        var postalCode: String = AddressJson.getString("vocabulary:postalCode")
        var countryCode: String = AddressJson.getString("vocabulary:countryCode")
        var stateDistrict: String = AddressJson.getString("vocabulary:stateDistrict")
        var streetAddress: String = AddressJson.getString("vocabulary:streetAddress")

        clearAddressFields()
        setAddressFromSearchResult(
            houseNumber,
            cityDistrict,
            stateDistrict,
            postalCode,
            countryCode,
            streetAddress
        )


        val geoLocationArray: JSONArray = propertiesData.getJSONArray("vocabulary:geo")

        var geoLocObject: String = geoLocationArray.getString(0)

        val geoLocJson: JSONObject = JSONObject(geoLocObject)

        var lattitude: Double = geoLocJson.getDouble("vocabulary:latitude")
        var longitude: Double = geoLocJson.getDouble("vocabulary:longitude")

        isChangeMarker = false
        clearMap()
        showMarker(LatLng(lattitude, longitude), "addressType")
        changeCameraPosition(LatLng(lattitude, longitude))
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun HomeFragment.setAddressFromSearchResult(
    houseNumber: String,
    cityDistrict: String,
    stateDistrict: String,
    postalCode: String,
    countryCode: String,
    streetAddress: String
) {

    binding!!.addNewAdd!!.edtUnit.setText(houseNumber)
    binding!!.addNewAdd!!.edtStreet.setText(streetAddress)
    binding!!.addNewAdd!!.edtCity.setText(cityDistrict)
    binding!!.addNewAdd!!.edtPincode.setText(postalCode)
    binding!!.addNewAdd!!.edtState.setText(stateDistrict)
    binding!!.addNewAdd!!.edtCountry.setText(countryCode)
    binding!!.addNewAdd!!.addressView.visibility = View.VISIBLE
    binding!!.addNewAdd!!.tvAddressText.text = Utility.returnFullAddress(houseNumber,"","",streetAddress,cityDistrict,stateDistrict,postalCode)



}

fun HomeFragment.selectedLabel(position: Int) {
    binding!!.addNewAdd!!.rlHomeIcon.setBackgroundResource(R.drawable.address_label_shape)
    binding!!.addNewAdd!!.rlOfficeIcon.setBackgroundResource(R.drawable.address_label_shape)
    binding!!.addNewAdd!!.rlAddLableIcon.setBackgroundResource(R.drawable.address_label_shape)
    binding!!.addNewAdd!!.ivHome.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.black2)))
    binding!!.addNewAdd!!.ivOffice.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.black2)))
    binding!!.addNewAdd!!.ivAddLabel.setImageTintList(
        ColorStateList.valueOf(
            resources.getColor(
                R.color.black2
            )
        )
    )
    binding!!.addNewAdd!!.tvAddLabel.setTextColor(resources.getColor(R.color.black2))
    binding!!.addNewAdd!!.tvOffice.setTextColor(resources.getColor(R.color.black2))
    binding!!.addNewAdd!!.tvHome.setTextColor(resources.getColor(R.color.black2))
    binding!!.addNewAdd!!.ivCheckAddLabel.setImageResource(R.drawable.labelunchecked)
    binding!!.addNewAdd!!.ivCheckOffice.setImageResource(R.drawable.labelunchecked)
    binding!!.addNewAdd!!.ivCheckHome.setImageResource(R.drawable.labelunchecked)

    binding!!.addNewAdd.addLabelView.visibility = View.GONE

    var img : Drawable? = null


    if (position == 0) {
        addressType = "home"
        binding!!.addNewAdd!!.rlHomeIcon.setBackgroundResource(R.drawable.label_selected_shape)
        binding!!.addNewAdd!!.ivHome.setImageTintList(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.purple1
                )
            )
        )
        binding!!.addNewAdd!!.tvHome.setTextColor(resources.getColor(R.color.purple1))
        binding!!.addNewAdd!!.ivCheckHome.setImageResource(R.drawable.labelcheck)
        img = getContext().getResources().getDrawable(R.drawable.home)

    } else if (position == 1) {
        addressType = "office"
        binding!!.addNewAdd!!.rlOfficeIcon.setBackgroundResource(R.drawable.label_selected_shape)
        binding!!.addNewAdd!!.ivOffice.setImageTintList(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.purple1
                )
            )
        )
        binding!!.addNewAdd!!.tvOffice.setTextColor(resources.getColor(R.color.purple1))
        binding!!.addNewAdd!!.ivCheckOffice.setImageResource(R.drawable.labelcheck)
        img = getContext().getResources().getDrawable(R.drawable.ic_office)
    } else if (position == 2) {
        binding!!.addNewAdd.addLabelView.visibility = View.VISIBLE
        addressType = "other"
        binding!!.addNewAdd!!.rlAddLableIcon.setBackgroundResource(R.drawable.label_selected_shape)
        binding!!.addNewAdd!!.ivAddLabel.setImageTintList(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.purple1
                )
            )
        )
        binding!!.addNewAdd!!.tvAddLabel.setTextColor(resources.getColor(R.color.purple1))
        binding!!.addNewAdd!!.ivCheckAddLabel.setImageResource(R.drawable.labelcheck)
        img = getContext().getResources().getDrawable(R.drawable.ic_add_lable)
    }

    binding!!.addNewAdd!!.tvaddressType.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
    binding!!.addNewAdd!!.tvaddressType.text = addressType
}

fun HomeFragment.clearAddressFields() {
    binding!!.addNewAdd!!.edtUnit.setText("")
    binding!!.addNewAdd!!.edtStreet.setText("")
    binding!!.addNewAdd!!.edtCity.setText("")
    binding!!.addNewAdd!!.edtPincode.setText("")
    binding!!.addNewAdd!!.edtState.setText("")
    binding!!.addNewAdd!!.edtCountry.setText("")
    binding!!.addNewAdd!!.edtBuildingName.setText("")
    binding!!.addNewAdd!!.edtBuildingNum.setText("")
    binding!!.addNewAdd!!.edtFloor.setText("")
    binding!!.addNewAdd!!.edtNeighbour.setText("")
}

fun HomeFragment.setCurrentAddress() {
    currentAddressText =
        reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties!!.place!!.name
    binding!!.addNewAdd!!.currentAddress.text = currentAddressText

}