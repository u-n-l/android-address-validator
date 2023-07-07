package com.unl.addressvalidator.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.geometry.LatLng
import com.unl.addressvalidator.model.reversegeocode.*
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

                    var name: String = propertiesData.getString("vocabulary:name")
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

                    featuresData.add(FeaturesData(addressType,geomateryData, PropertiesData("",name,
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

}