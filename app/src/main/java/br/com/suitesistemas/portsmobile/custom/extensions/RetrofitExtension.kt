package br.com.suitesistemas.portsmobile.custom.extensions

import br.com.suitesistemas.portsmobile.custom.retrofit.callback
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.utils.ErrorBodyUtils
import retrofit2.Call

fun <T> Call<T>.responseHandle(expectedCode: Int, callResponse: (ApiResponse<T?>) -> Unit) {
    enqueue(callback { response, error ->
        response?.let {
            if (response.code() == expectedCode) {
                callResponse(ApiResponse(response.body()))
            } else {
                callResponse(ApiResponse(ErrorBodyUtils.handler(response.errorBody())))
            }
        }
        error?.message?.let {
            callResponse(ApiResponse(it))
        }
    })
}

fun <T> Call<T>.responseHandle(success: () -> Unit, failure: (messageError: String?) -> Unit, expectedCode: Int = 204) {
    enqueue(callback { response, error ->
        response?.let {
            if (it.code() == expectedCode) {
                success()
            } else {
                it.errorBody()?.let { errorBody ->
                    failure(ErrorBodyUtils.handler(errorBody))
                }
            }
        }
        error?.message?.let {
            failure(it)
        }
    })
}

fun <T> Call<T>.responseHandle(expectedCode: Int,
                               success: (body: T?) -> Unit,
                               failure: (messageError: String?) -> Unit,
                               finished: () -> Unit) {
    enqueue(callback { response, error ->
        response?.let {
            if (response.code() == expectedCode) {
                success(response.body())
            } else {
                failure(ErrorBodyUtils.handler(response.errorBody()))
            }
            finished()
        }
        error?.message?.let {
            failure(it)
            finished()
        }
    })
}