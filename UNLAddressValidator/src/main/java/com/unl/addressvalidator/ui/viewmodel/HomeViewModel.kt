package com.unl.addressvalidator.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabasewithmodelclassess.model.ImageUploadResponse
import com.google.gson.JsonObject
import com.unl.addressvalidator.database.UnlAddressDatabase
import com.unl.addressvalidator.model.autocomplet.AutocompleteResponse
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.network.RetrofitClient
import com.unl.addressvalidator.network.RetrofitImageUploadClient
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File


class HomeViewModel : ViewModel()
{
    val imageUploadResponseData : MutableLiveData<ApiCallBack<ImageUploadResponse>> = MutableLiveData()
    val addressJson : MutableLiveData<ApiCallBack<JsonObject>> = MutableLiveData()
    val autoCompleteData : MutableLiveData<ApiCallBack<AutocompleteResponse>> = MutableLiveData()
    val reverseGeocodeData : MutableLiveData<ApiCallBack<ReverseGeoCodeResponse>> = MutableLiveData()
    val reverseGeocodeDataCurrentLocation : MutableLiveData<ApiCallBack<ReverseGeoCodeResponse>> = MutableLiveData()
    val nearbyLandmark : MutableLiveData<ApiCallBack<JsonObject>> = MutableLiveData()
     var addresslist: LiveData<List<CreateAddressModel>> = MutableLiveData()


    fun getAutocompleteData(text : String,location : JsonObject) = viewModelScope.launch {
        getAutocompleteResult(text,location)
    }


    fun getAddressFromItemId(itemId : String) = viewModelScope.launch {
        getAddressFromItemID(itemId)
    }


    fun getAddfromLocation(location : JsonObject) = viewModelScope.launch {
        getAddressFromLocation(location)
    }

    fun getAddfromCurrentLocation(location : JsonObject) = viewModelScope.launch {
        getAddressFromCurrentLocation(location)
    }

    fun getNearbyLandmark(location : JsonObject) = viewModelScope.launch {
        getNearbyLandmarks(location)
    }

    fun uploadImage(file :  MultipartBody.Part) = viewModelScope.launch {
       // uploadImageFile(file)
        uploadImageWithPart(file)
    }


    fun insertAddress(database : UnlAddressDatabase, addressmodel : CreateAddressModel)
    {
        viewModelScope.launch {
            database.createAddressDao().insertAddress(addressmodel)

        }
    }

    fun getAddress(database : UnlAddressDatabase)
    {
        addresslist = database.createAddressDao().getAddressData()
    }

    private suspend fun getAutocompleteResult(text : String,location : JsonObject){
        autoCompleteData.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getAutocompleteResult(text,location)
        autoCompleteData.postValue(handleAutocompleteResponse(response))
    }

    private suspend fun getAddressFromItemID(itemId : String){
        addressJson.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getAddressFromItemId(itemId)
        addressJson.postValue(handleAddressFromItemIDResponse(response))
    }

    private suspend fun getAddressFromLocation(location : JsonObject){
        reverseGeocodeData.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getReverseGeocode(location)
        reverseGeocodeData.postValue(handleReverseGeoCOdeResponse(response))
    }

    private suspend fun getAddressFromCurrentLocation(location : JsonObject){
        reverseGeocodeDataCurrentLocation.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getReverseGeocode(location)
        reverseGeocodeDataCurrentLocation.postValue(handleReverseGeoCOdeResponse(response))
    }

    private suspend fun getNearbyLandmarks(location : JsonObject){
        nearbyLandmark.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getLandmark("LandmarksOrHistoricalBuildings",location)
        nearbyLandmark.postValue(handleNearbyLandmarkResponse(response))
    }
    private suspend fun uploadImageWithPart(file : MultipartBody.Part){
       try {
           imageUploadResponseData.postValue(ApiCallBack.Loading())
           val response = RetrofitImageUploadClient.apiInterface.uploadImagewithPart(file)
           imageUploadResponseData.postValue(handleImageUploadResponse(response))
       }
       catch (e:java.lang.Exception)
       {
           e.printStackTrace()
       }
    }

    private fun handleReverseGeoCOdeResponse(response: Response<ReverseGeoCodeResponse>): ApiCallBack<ReverseGeoCodeResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return ApiCallBack.Success(resultResponse)
            }
        }
        return ApiCallBack.Error(response.message())
    }

    private fun handleAutocompleteResponse(response: Response<AutocompleteResponse>): ApiCallBack<AutocompleteResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return ApiCallBack.Success(resultResponse)
            }
        }
        return ApiCallBack.Error(response.message())
    }
    private fun handleAddressFromItemIDResponse(response: Response<JsonObject>): ApiCallBack<JsonObject> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return ApiCallBack.Success(resultResponse)
            }
        }
        return ApiCallBack.Error(response.message())
    }

    private fun handleNearbyLandmarkResponse(response: Response<JsonObject>): ApiCallBack<JsonObject> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return ApiCallBack.Success(resultResponse)
            }
        }
        return ApiCallBack.Error(response.message())
    }


    private fun handleImageUploadResponse(response: Response<ImageUploadResponse>): ApiCallBack<ImageUploadResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return ApiCallBack.Success(resultResponse)
            }
        }
        return ApiCallBack.Error(response.message())
    }
}