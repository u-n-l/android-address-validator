package com.unl.addressvalidator.network


import com.unl.map.sdk.data.API_KEY
import com.unl.map.sdk.data.VPM_ID
import com.unl.map.sdk.prefs.DataManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * [SigV4Interceptor] is an [Interceptor] for Authorization of UNL credentials
 *
 * @property api_key is used to load map style
 * @property vpm_id is Virtual Private Map ID
 * @constructor
 */
class SigV4Interceptor(var api_key: String, var vpm_id: String) : Interceptor {

    /**
     * Intercept method is used to set header values to an authorized style for Map
     *
     * @return Returns the Response for Map Style
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        /**
         * [Request] Object is used to send data to server like [api_key] and [vpm_id]
         */
        val request = original.newBuilder()
            .addHeader("Accept", "application/json")
            .header("Content-Type", "application/json" )
            .header(API_KEY, api_key)
            .header(VPM_ID, vpm_id)
            .method(original.method, original.body)
            .build()

        return chain.proceed(request)
    }

}
