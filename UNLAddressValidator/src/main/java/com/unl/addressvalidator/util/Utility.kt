package com.unl.addressvalidator.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.View
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.reversegeocode.*
import com.unl.map.sdk.data.CellPrecision
import com.unl.map.sdk.helpers.grid_controls.loadGrids
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import com.unl.map.sdk.views.UnlMapView
import org.json.JSONArray
import org.json.JSONObject

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

        if(!houseNo.isNullOrEmpty() )
        {
            address = houseNo + ", "
        }

        if(!floorNo.isNullOrEmpty() )
        {
            address = address + floorNo + " floor, "
        }
        if(!builingName.isNullOrEmpty() )
        {
            address = address + builingName + ", "
        }
        if(!street.isNullOrEmpty() )
        {
            address = address + street + ", "
        }

        if(!city.isNullOrEmpty() )
        {
            address = address + city + ", "
        }

        if(!state.isNullOrEmpty() )
        {
            address = address + state + ", "
        }
        if(!pincode.isNullOrEmpty() )
        {
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

        if(!houseNo.isNullOrEmpty() )
        {
            address = houseNo + ", "
        }

        if(!floorNo.isNullOrEmpty() )
        {
            address = address + floorNo + " floor, "
        }
        if(!builingNo.isNullOrEmpty() )
        {
            address = address + builingNo + ", "
        }
        if(!builingName.isNullOrEmpty() )
        {
            address = address + builingName + ", "
        }
        if(!street.isNullOrEmpty() )
        {
            address = address + street + ", "
        }

        if(!neighbour.isNullOrEmpty() )
        {
            address = address + neighbour + ", "
        }

        if(!city.isNullOrEmpty() )
        {
            address = address + city + ", "
        }

        if(!state.isNullOrEmpty() )
        {
            address = address + state + ", "
        }
        if(!countryCode.isNullOrEmpty() )
        {
            address = address + countryCode + ", "
        }
        if(!pincode.isNullOrEmpty() )
        {
            address = address + pincode + ", "
        }

        return address
    }



    fun parseNearbyLandmarkJson(rawData: JsonObject) : ArrayList<ReverseGeoCodeResponse>{

        var landmarkList = ArrayList<ReverseGeoCodeResponse>()

        try {
            val parsed = JSONObject(rawData.toString())
            val featuresArray: JSONArray = parsed.getJSONArray("geojson:Features")
            if(featuresArray!= null && featuresArray.length()>0)
            {
                for (i in 0 .. featuresArray.length() )
                {
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

                    var postalAddressData = PostalAddressData(countryCode,stateDistrict,cityDistrict,addressRegion,streetAddress,postalCode,houseNumber)

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

                    var geomateryData = GeomateryData("",geoData)

                    var featuresData = ArrayList<FeaturesData>()
                    var contributorInfoData = ArrayList<ContributorInfoData>()
                    contributorInfoData.add(ContributorInfoData("",""))

                    featuresData.add(FeaturesData(addressType,geomateryData, PropertiesData("",categoryType,businessName,
                        PlaceData("",""),
                        postaAddList,UnlLocationData("",""),contributorInfoData)))

                    landmarkList.add(ReverseGeoCodeResponse(addressType,featuresData))

                }

            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return landmarkList
    }

    fun getImagePathFromUri(contentURI : Uri, ct : Context) : String?
    {
      var   result : String? = ""
       var  cursor : Cursor? = ct.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            var idx : Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    fun changeCameraPosition(latlng: LatLng,mapbox : MapboxMap? ) {
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

    fun configureMap(mapView : UnlMapView, context : Context)
    {
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
       }
       catch (e:Exception)
       {
           e.printStackTrace()
       }
    }

    fun returnRandomDigit() : Long
    {
        var randomNum : Long = 0
        var fromNumber : Long = 10000000
        var toNumber : Long = 99999999
        randomNum =   (fromNumber.rangeTo(toNumber)).random()
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


}