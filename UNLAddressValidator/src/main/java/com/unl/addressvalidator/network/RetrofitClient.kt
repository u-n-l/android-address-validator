package com.unl.addressvalidator.network

import com.unl.addressvalidator.util.Constant.BASE_URL
import com.unl.addressvalidator.util.Constant.IMAGE_UPLOAD_BASE_URL
import com.unl.map.sdk.SigV4Interceptor
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

object RetrofitClient
{
    val apiInterface: APIInterface by lazy {
        retrofit.create(APIInterface::class.java)
    }


    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder() .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .addInterceptor(SigV4Interceptor(DataManager.getApiKey()?:"", DataManager.getVpmId()?:""))
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }


}