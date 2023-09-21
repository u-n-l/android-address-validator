package com.unl.addressvalidator.network

import com.example.roomdatabasewithmodelclassess.model.CreateAddressResponse
import com.example.roomdatabasewithmodelclassess.model.ImageUploadResponse
import com.google.gson.JsonObject
import com.unl.addressvalidator.model.autocomplet.AutocompleteResponse
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*
import java.io.File


interface APIInterface {
    @GET("autocomplete")
    suspend fun getAutocompleteResult(@Query("query") query : String, @Query("location") location: JsonObject?): Response<AutocompleteResponse>
    @GET("autocomplete")
    suspend fun getAutocompleteResult(@Query("query") query : String): Response<AutocompleteResponse>

    @GET("geocode/reverse")
    suspend  fun getReverseGeocode(@Query("location") param: JsonObject?) : Response<ReverseGeoCodeResponse>

    @GET("search/place/{item_id}")
    suspend  fun getAddressFromItemId(@Path("item_id") param: String?) : Response<JsonObject>

    @GET("search")
    suspend  fun getLandmark(@Query("query") query: String? , @Query("boundary") boundary: JsonObject? ) : Response<JsonObject>

    @Multipart
    @POST("upload/asset")
    suspend  fun uploadImagewithPart(@Part  file : MultipartBody.Part ) : Response<ImageUploadResponse>


    @POST("create/address")
    suspend  fun addNewAddress(@Body params : RequestBody) : Response<CreateAddressResponse>


}