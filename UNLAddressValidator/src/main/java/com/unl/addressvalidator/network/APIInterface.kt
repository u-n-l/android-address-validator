package com.unl.addressvalidator.network

import com.example.roomdatabasewithmodelclassess.model.ImageUploadResponse
import com.google.gson.JsonObject
import com.unl.addressvalidator.model.autocomplet.AutocompleteResponse
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
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
    suspend  fun uploadImage(@Part ("file") file : File? ) : Response<String>

    @Multipart
    @POST("upload/asset")
    suspend  fun uploadImagewithPart(@Part  file : MultipartBody.Part ) : Response<ImageUploadResponse>


}