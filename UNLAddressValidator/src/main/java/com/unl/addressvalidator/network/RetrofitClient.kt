package com.unl.addressvalidator.network

import com.unl.addressvalidator.util.Constant.BASE_URL
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

object RetrofitClient
{
    val apiInterface: APIInterface by lazy {
        retrofit.create(APIInterface::class.java)
    }


    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()

                    val requestBuilder: Request.Builder = original.newBuilder()
                  // requestBuilder.addHeader("Authorization", "Client-ID 7c20820e4252d22")
                    requestBuilder.addHeader("Accept", "application/json")
                    requestBuilder.addHeader(API_KEY, DataManager.getApiKey()?:"")
                    requestBuilder.addHeader(VPM_ID, DataManager.getVpmId()?:"")
                    val request: Request = requestBuilder.build()
                    return chain.proceed(request)
                } })
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val RetrofitClient by lazy {
        retrofit.create(APIInterface::class.java)
    }
}