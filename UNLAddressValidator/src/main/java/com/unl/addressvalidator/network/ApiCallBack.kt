package com.unl.addressvalidator.network

import java.util.stream.Stream

sealed class ApiCallBack<T>(val data : T? = null,val message : String? = null){
    class Success<T>(data : T) : ApiCallBack<T>(data)
    class Error<T>(message: String,data : T? = null) : ApiCallBack<T>(data,message)
    class Loading<T> : ApiCallBack<T>()
}
