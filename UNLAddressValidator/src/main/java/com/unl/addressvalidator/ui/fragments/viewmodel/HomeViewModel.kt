package com.unl.addressvalidator.ui.fragments.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unl.addressvalidator.model.AutocompleteData
import com.unl.addressvalidator.model.AutocompleteResponse
import com.unl.addressvalidator.network.APIInterface
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response


class HomeViewModel : ViewModel()
{
    val autoCompleteData : MutableLiveData<ApiCallBack<AutocompleteResponse>> = MutableLiveData()

    fun getAutocompleteData(text : String) = viewModelScope.launch {
        getAutocompleteResult(text)
    }

    private suspend fun getAutocompleteResult(text : String){

        autoCompleteData.postValue(ApiCallBack.Loading())
        val response = RetrofitClient.apiInterface.getAutocompleteResult(text)
        autoCompleteData.postValue(handleAutocompleteResponse(response))
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