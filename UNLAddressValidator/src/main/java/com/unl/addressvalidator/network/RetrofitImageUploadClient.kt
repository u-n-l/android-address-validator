package com.unl.addressvalidator.network

import com.unl.addressvalidator.util.Constant.BASE_URL
import com.unl.addressvalidator.util.Constant.IMAGE_UPLOAD_BASE_URL
import com.unl.map.sdk.data.API_KEY
import com.unl.map.sdk.data.VPM_ID
import com.unl.map.sdk.networks.UnlMapApi
import com.unl.map.sdk.prefs.DataManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitImageUploadClient
{
    val apiInterface: APIInterface by lazy {
        retrofit.create(APIInterface::class.java)
    }


    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder() .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()

                    val requestBuilder: Request.Builder = original.newBuilder()
                  // requestBuilder.addHeader("Authorization", "Client-ID 7c20820e4252d22")
                   // requestBuilder.header("Content-Type", "image/png" )
                  //  requestBuilder.header("Accept", "multipart/form-data" )
                    //requestBuilder.header("Accept", "image/*" )
                   // requestBuilder.header("Content-Type", "image/*" )
                    requestBuilder.header("Content-Type", "application/json" )
                    requestBuilder.addHeader(API_KEY, "VhvEgf1pvXXo5AllXzQfBrb0ZQlO8Laq")
                    requestBuilder.addHeader(VPM_ID, "02cf55f3-d0eb-4da9-9cb2-1dcc504b054f")
                    val request: Request = requestBuilder.build()
                    return chain.proceed(request)
                } })
            .build()

        Retrofit.Builder()
            .baseUrl(IMAGE_UPLOAD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }


}