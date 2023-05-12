package com.unl.addressvalidator.network

import com.google.gson.JsonObject
import com.unl.addressvalidator.model.autocomplet.AutocompleteResponse
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface APIInterface {
    @GET("autocomplete")
    suspend fun getAutocompleteResult(@Query("query") query : String): Response<AutocompleteResponse>

    @GET("geocode/reverse")
    suspend  fun getReverseGeocode(@Query("location") param: JsonObject?) : Response<ReverseGeoCodeResponse>


}