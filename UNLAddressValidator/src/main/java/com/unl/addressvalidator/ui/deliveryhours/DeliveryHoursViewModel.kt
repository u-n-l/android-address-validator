package com.unl.addressvalidator.ui.deliveryhours

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabasewithmodelclassess.model.CreateAddressResponse
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
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File


class DeliveryHoursViewModel : ViewModel()
{

    val imageUploadResponseData : MutableLiveData<ApiCallBack<ImageUploadResponse>> = MutableLiveData()
    val createAddressResponseData : MutableLiveData<ApiCallBack<CreateAddressResponse>> = MutableLiveData()

    fun insertAddress(database : UnlAddressDatabase, addressmodel : CreateAddressModel)
    {
        viewModelScope.launch {
            database.createAddressDao().insertAddress(addressmodel)

        }
    }


    fun uploadImage(file :  MultipartBody.Part) = viewModelScope.launch {
        uploadImageWithPart(file)
    }


    fun addNewAddress(params : RequestBody) = viewModelScope.launch {
        createNewAddress(params)

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


    private fun handleImageUploadResponse(response: Response<ImageUploadResponse>): ApiCallBack<ImageUploadResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return ApiCallBack.Success(resultResponse)
            }
        }
        return ApiCallBack.Error(response.message())
    }



    fun addAddress(file :  MultipartBody.Part) = viewModelScope.launch {
        uploadImageWithPart(file)
    }


    private suspend fun createNewAddress(params : RequestBody){
        try {
            createAddressResponseData.postValue(ApiCallBack.Loading())
            val response = RetrofitImageUploadClient.apiInterface.addNewAddress(params)
            createAddressResponseData.postValue(handleCreateAddressResponse(response))
        }
        catch (e:java.lang.Exception)
        {
            e.printStackTrace()
        }
    }

    private fun handleCreateAddressResponse(response: Response<CreateAddressResponse>): ApiCallBack<CreateAddressResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return ApiCallBack.Success(resultResponse)
            }
        }
        return ApiCallBack.Error(response.message())
    }


}