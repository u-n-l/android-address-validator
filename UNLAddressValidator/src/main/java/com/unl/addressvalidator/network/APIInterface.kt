package com.unl.addressvalidator.network

import com.unl.addressvalidator.model.AutocompleteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    @GET("autocomplete")
    suspend fun getAutocompleteResult(@Query("query") query : String): Response<AutocompleteResponse>
}