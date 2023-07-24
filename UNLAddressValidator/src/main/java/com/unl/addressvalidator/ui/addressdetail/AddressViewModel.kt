package com.unl.addressvalidator.ui.addressdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


class AddressViewModel : ViewModel()
{

     var addresslist: LiveData<List<CreateAddressModel>> = MutableLiveData()

    fun getAddress(database : UnlAddressDatabase)
    {
        addresslist = database.createAddressDao().getAddressData()
    }

}