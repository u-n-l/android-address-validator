package com.unl.addressvalidator.util

import android.Manifest
import android.R.attr.button
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.address.AddressRequestModel
import com.unl.addressvalidator.model.reversegeocode.*
import com.unl.addressvalidator.util.Constant.CAMERA_PERM_CODE
import com.unl.addressvalidator.util.Constant.CAMERA_REQUEST_CODE
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_BUILDING_NUMBER
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_CITY
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_COLONY_NAME
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_COUNTRY
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_FLOOR
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_LATITUDE
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_LONGITUDE
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_NAME
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_NEIGHBOURHOOD
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_NUMBER
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_POSTALCODE
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_STATE
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_STREET
import com.unl.addressvalidator.util.Constant.KEY_ADDRESS_TYPE
import com.unl.map.sdk.data.CellPrecision
import com.unl.map.sdk.helpers.grid_controls.loadGrids
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import com.unl.map.sdk.views.UnlMapView
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream


object Utility {


    fun returnFullAddress(
        houseNo: String?,
        floorNo: String?,
        builingName: String?,
        street: String?,
        city: String?,
        state: String?,
        pincode: String?
    ): String? {
        var address = ""

        if (!houseNo.isNullOrEmpty()) {
            address = houseNo + ", "
        }

        if (!floorNo.isNullOrEmpty()) {
            address = address + floorNo + " floor, "
        }
        if (!builingName.isNullOrEmpty()) {
            address = address + builingName + ", "
        }
        if (!street.isNullOrEmpty()) {
            address = address + street + ", "
        }

        if (!city.isNullOrEmpty()) {
            address = address + city + ", "
        }

        if (!state.isNullOrEmpty()) {
            address = address + state + ", "
        }
        if (!pincode.isNullOrEmpty()) {
            address = address + pincode + ", "
        }

        return address
    }

    fun returnUNLFullAddress(
        houseNo: String?,
        floorNo: String?,
        builingNo: String?,
        builingName: String?,
        street: String?,
        neighbour: String?,
        city: String?,
        state: String?,
        pincode: String?,
        countryCode: String?
    ): String? {
        var address = ""

        if (!houseNo.isNullOrEmpty()) {
            address = houseNo + ", "
        }

        if (!floorNo.isNullOrEmpty()) {
            address = address + floorNo + " floor, "
        }
        if (!builingNo.isNullOrEmpty()) {
            address = address + builingNo + ", "
        }
        if (!builingName.isNullOrEmpty()) {
            address = address + builingName + ", "
        }
        if (!street.isNullOrEmpty()) {
            address = address + street + ", "
        }

        if (!neighbour.isNullOrEmpty()) {
            address = address + neighbour + ", "
        }

        if (!city.isNullOrEmpty()) {
            address = address + city + ", "
        }

        if (!state.isNullOrEmpty()) {
            address = address + state + ", "
        }
        if (!countryCode.isNullOrEmpty()) {
            address = address + countryCode + ", "
        }
        if (!pincode.isNullOrEmpty()) {
            address = address + pincode + ", "
        }

        return address
    }


    fun parseNearbyLandmarkJson(rawData: JsonObject): ArrayList<ReverseGeoCodeResponse> {

        var landmarkList = ArrayList<ReverseGeoCodeResponse>()

        try {
            val parsed = JSONObject(rawData.toString())
            val featuresArray: JSONArray = parsed.getJSONArray("geojson:Features")
            if (featuresArray != null && featuresArray.length() > 0) {
                for (i in 0..featuresArray.length()) {
                    var featureArrayJson: String = featuresArray.getString(i)
                    val featureJson: JSONObject = JSONObject(featureArrayJson)


                    var propertiesData: JSONObject = featureJson.getJSONObject("geojson:properties")

                    var categoryType: String = propertiesData.getString("@type")
                    var businessName: String = propertiesData.getString("vocabulary:name")
                    //Address Parsing
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

                    var postalAddressData = PostalAddressData(
                        countryCode,
                        stateDistrict,
                        cityDistrict,
                        addressRegion,
                        streetAddress,
                        postalCode,
                        houseNumber
                    )

                    var postaAddList = ArrayList<PostalAddressData>()
                    postaAddList.add(postalAddressData)
                    //Geo Parsing
                    val geoLocationArray: JSONArray = propertiesData.getJSONArray("vocabulary:geo")
                    var geoLocObject: String = geoLocationArray.getString(0)
                    val geoLocJson: JSONObject = JSONObject(geoLocObject)

                    var lattitude: Double = geoLocJson.getDouble("vocabulary:latitude")
                    var longitude: Double = geoLocJson.getDouble("vocabulary:longitude")

                    var geoData = ArrayList<Double>()
                    geoData.add(lattitude)
                    geoData.add(longitude)

                    var geomateryData = GeomateryData("", geoData)

                    var featuresData = ArrayList<FeaturesData>()
                    var contributorInfoData = ArrayList<ContributorInfoData>()
                    contributorInfoData.add(ContributorInfoData("", ""))

                    featuresData.add(
                        FeaturesData(
                            addressType, geomateryData, PropertiesData(
                                "", categoryType, businessName,
                                PlaceData("", ""),
                                postaAddList, UnlLocationData("", ""), contributorInfoData
                            )
                        )
                    )

                    landmarkList.add(ReverseGeoCodeResponse(addressType, featuresData))

                }

            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return landmarkList
    }

    fun getImagePathFromUri(contentURI: Uri, ct: Context): String? {
        var result: String? = ""
        var cursor: Cursor? = ct.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            var idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    fun changeCameraPosition(latlng: LatLng, mapbox: MapboxMap?) {
        val latLngBounds = LatLngBounds.Builder()
            .include(latlng)
            .include(latlng)
            .build()

        mapbox!!.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                50
            )
        )
        mapbox!!.setMaxZoomPreference(21.00)
    }

    fun configureMap(mapView: UnlMapView, context: Context) {
        try {
            mapView.enableTileSelector(true)
            mapView.setGridControls(context, true)
            mapView.setTileSelectorGravity(Gravity.END)
            mapView.ivTile.setImageResource(R.drawable.ic_tile)
            mapView.ivTile.setBackgroundColor(Color.parseColor("#00000000"))
            mapView.mapbox!!.uiSettings.setCompassFadeFacingNorth(true)
            mapView.mapbox!!.uiSettings.setCompassImage(context.resources.getDrawable(R.drawable.transparent_bg))
            mapView.isVisibleGrids = true
            mapView.isVisibleTiles = false
            mapView.cellPrecision = CellPrecision.GEOHASH_LENGTH_9
            mapView.mapbox?.loadGrids(true, mapView, CellPrecision.GEOHASH_LENGTH_9)
            mapView.ivGrid.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun returnRandomDigit(): Long {
        var randomNum: Long = 0
        var fromNumber: Long = 10000000
        var toNumber: Long = 99999999
        randomNum = (fromNumber.rangeTo(toNumber)).random()
        return randomNum
    }

    fun convert(coord: Double): String? {
        var coord = coord
        coord = Math.abs(coord)
        val degrees = coord.toInt()
        coord = (coord - degrees) * 60
        val minutes = coord.toInt()
        coord = (coord - minutes) * 60
        val seconds = (coord * 1000).toInt()
        return "$degrees/1,$minutes/1,$seconds/1000"
    }

    fun convertObjIntoJson(myObj: AddressRequestModel): String {
        val gson = Gson()
        val json = gson.toJson(myObj)
        return json
    }

    fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    open fun askCameraPermissions(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                CAMERA_PERM_CODE
            )
        } else {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("SuspiciousIndentation")
    fun getAddressBody(addressData: AddressRequestModel): RequestBody {
        var builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        try {

            builder.addFormDataPart(KEY_ADDRESS, addressData.address!!)
            if (!addressData.addressModel!!.houseNumber.isNullOrEmpty())
                builder.addFormDataPart(
                    KEY_ADDRESS_NUMBER,
                    addressData.addressModel!!.houseNumber!!
                )

            if (!addressData.addressModel!!.floor.isNullOrEmpty())
                builder.addFormDataPart(KEY_ADDRESS_FLOOR, addressData.addressModel!!.floor!!)

            if (!addressData.addressModel!!.builingName.isNullOrEmpty())
                builder.addFormDataPart(KEY_ADDRESS_NAME, addressData.addressModel!!.builingName!!)

            if (!addressData.addressModel!!.street.isNullOrEmpty())
                builder.addFormDataPart(KEY_ADDRESS_STREET, addressData.addressModel!!.street!!)

            if (!addressData.addressModel!!.street.isNullOrEmpty())
                builder.addFormDataPart(
                    KEY_ADDRESS_COLONY_NAME,
                    addressData.addressModel!!.street!!
                )

            if (!addressData.addressModel!!.city.isNullOrEmpty())
                builder.addFormDataPart(KEY_ADDRESS_CITY, addressData.addressModel!!.city!!)

            if (!addressData.addressModel!!.state.isNullOrEmpty())
                builder.addFormDataPart(KEY_ADDRESS_STATE, addressData.addressModel!!.state!!)

            if (!addressData.addressModel!!.country.isNullOrEmpty())
                builder.addFormDataPart(KEY_ADDRESS_COUNTRY, addressData.addressModel!!.country!!)

            if (!addressData.addressModel!!.postalCode.isNullOrEmpty())
                builder.addFormDataPart(
                    KEY_ADDRESS_POSTALCODE,
                    addressData.addressModel!!.postalCode!!
                )

            if (!addressData.addressModel!!.builingNumber.isNullOrEmpty())
                builder.addFormDataPart(
                    KEY_ADDRESS_BUILDING_NUMBER,
                    addressData!!.addressModel!!.builingNumber!!
                )

            if (!addressData.addressModel!!.neighbourhood.isNullOrEmpty())
                builder.addFormDataPart(
                    KEY_ADDRESS_NEIGHBOURHOOD,
                    addressData!!.addressModel!!.neighbourhood!!
                )

            builder.addFormDataPart(
                KEY_ADDRESS_LATITUDE,
                "" + addressData.locationModel!!.lattitude
            )
            builder.addFormDataPart(
                KEY_ADDRESS_LONGITUDE,
                "" + addressData.locationModel!!.longitude
            )
            builder.addFormDataPart(KEY_ADDRESS_TYPE, addressData!!.addressType!!)


            if (addressData!!.openingHoursSpecificationModel != null && addressData!!.openingHoursSpecificationModel!!.size > 0) {
                for (i in 0..addressData!!.openingHoursSpecificationModel!!.size - 1) {
                    builder.addFormDataPart(
                        "openingHoursSpecification[" + i + "].opens",
                        addressData!!.openingHoursSpecificationModel!!.get(i).opens
                    )
                    builder.addFormDataPart(
                        "openingHoursSpecification[" + i + "].closes",
                        addressData!!.openingHoursSpecificationModel!!.get(i).closes
                    )
                    builder.addFormDataPart(
                        "openingHoursSpecification[" + i + "].day_of_week",
                        addressData!!.openingHoursSpecificationModel!!.get(i).dayOfWeek
                    )
                    if (addressData!!.openingHoursSpecificationModel!!.get(i).isHoliday)
                        builder.addFormDataPart(
                            "openingHoursSpecification[" + i + "].is_holiday",
                            "True"
                        )
                    else
                        builder.addFormDataPart(
                            "openingHoursSpecification[" + i + "].is_holiday",
                            "False"
                        )
                }
            }

            if (addressData!!.images != null && addressData!!.images!!.size > 0)
            {
                for (i in 0..addressData!!.images!!.size - 1)
                {
                    builder.addFormDataPart(
                        "images[" + i + "].id",
                        addressData!!.images!!.get(i)
                    )
                }
            }




            if (addressData!!.landmarkModel != null && addressData!!.landmarkModel!!.size > 0) {
                for (i in 0..addressData!!.landmarkModel!!.size - 1)
                {
                    builder.addFormDataPart(
                        "landmarks[" + i + "].landmark_name",
                        addressData!!.landmarkModel!!.get(i).landmark_name
                    )
                    builder.addFormDataPart(
                        "landmarks[" + i + "]._type",
                        addressData!!.landmarkModel!!.get(i).type
                    )
                    builder.addFormDataPart(
                        "landmarks[" + i + "].location.latitude",
                        addressData!!.landmarkModel!!.get(i).lattitude
                    )
                    builder.addFormDataPart(
                        "landmarks[" + i + "].location.longitude",
                        addressData!!.landmarkModel!!.get(i).longitude
                    )

                    if (addressData!!.landmarkModel!!.get(i).imageList != null && addressData!!.landmarkModel!!.get(i).imageList.size > 0)
                    {
                        for (j in 0..addressData!!.landmarkModel!!.get(i).imageList.size - 1)
                        {
                            builder.addFormDataPart(
                                "landmarks[" + i + "]."+"images[" + j + "].id",
                                addressData!!.landmarkModel!!.get(i).imageList!!.get(j)
                            )
                        }
                    }


                }
            }


            if (addressData!!.entranceModel != null && addressData!!.entranceModel!!.size > 0) {
                for (i in 0..addressData!!.entranceModel!!.size - 1) {
                    builder.addFormDataPart(
                        "entrance[" + i + "].entrance_no",
                        addressData!!.entranceModel!!.get(i).entranceNo
                    )
                    builder.addFormDataPart(
                        "entrance[" + i + "].entrance_name",
                        addressData!!.entranceModel!!.get(i).entranceName
                    )

                    if (addressData!!.entranceModel!!.get(i).imageArray != null && addressData!!.entranceModel!!.get(i).imageArray.size > 0)
                    {
                        for (j in 0..addressData!!.landmarkModel!!.get(i).imageList.size - 1)
                        {
                            builder.addFormDataPart(
                                "entrance[" + i + "]."+"images[" + j + "].id",
                                addressData!!.entranceModel!!.get(i).imageArray!!.get(j)
                            )
                        }
                    }
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        return builder.build()

    }
}