package br.com.suitesistemas.portsmobile.service.product_color

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.ApiResponse

class ProductColorRepository(private val companyName: String) {

    private val service = RetrofitConfig().productColorService()

    fun findBy(productId: String,
               success: (customers: List<ProductColor>?) -> Unit,
               failure: (messageError: String?) -> Unit,
               finished: () -> Unit) {
        val call = service.find(companyName, productId)
        call.responseHandle(200, {
            success(it)
        }, {
            failure(it)
        }, {
            finished()
        })
    }

    fun findBy(productId: String): MutableLiveData<ApiResponse<MutableList<ProductColor>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
        val call = service.find(companyName, productId)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun insert(productColors: MutableList<ProductColor>): MutableLiveData<ApiResponse<MutableList<ProductColor>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
        val call = service.insert(companyName, productColors)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun delete(colorId: String, productId: String): MutableLiveData<ApiResponse<Boolean?>> {
        val apiResponse = MutableLiveData<ApiResponse<Boolean?>>()
        val call = service.delete(colorId, productId, companyName)

        call.responseHandle(204) {
            apiResponse.value = ApiResponse(true)
        }

        return apiResponse
    }

}