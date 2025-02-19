package br.com.suitesistemas.portsmobile.custom.retrofit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> callback(callResponse: (response: Response<T>?, failure:  Throwable?) -> Unit): Callback<T> {
    return object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            callResponse(response, null)
        }

        override fun onFailure(call: Call<T>, error: Throwable) {
            callResponse(null, error)
        }

    }
}