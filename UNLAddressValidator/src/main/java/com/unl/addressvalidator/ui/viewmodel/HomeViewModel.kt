package com.unl.addressvalidator.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.unl.addressvalidator.model.autocomplet.AutocompleteResponse
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response


class HomeViewModel : ViewModel()
{
    val autoCompleteData : MutableLiveData<ApiCallBack<AutocompleteResponse>> = MutableLiveData()
    val reverseGeocodeData : MutableLiveData<ApiCallBack<ReverseGeoCodeResponse>> = MutableLiveData()

    fun getAutocompleteData(text : String) = viewModelScope.launch {
        getAutocompleteResult(text)
    }

    fun getAddfromLocation(text : JsonObject) = viewModelScope.launch {
        getAddressFromLocation(text)
    }

    private suspend fun getAutocompleteResult(text : String){
        autoCompleteData.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getAutocompleteResult(text)
        autoCompleteData.postValue(handleAutocompleteResponse(response))
    }

    private suspend fun getAddressFromLocation(text : JsonObject){
        reverseGeocodeData.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getReverseGeocode(text)
        reverseGeocodeData.postValue(handleReverseGeoCOdeResponse(response))
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
}