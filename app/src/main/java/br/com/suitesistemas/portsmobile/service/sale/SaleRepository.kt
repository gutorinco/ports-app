package br.com.suitesistemas.portsmobile.service.sale

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse

class SaleRepository(private val companyName: String) {

    fun findAll(): MutableLiveData<ApiResponse<MutableList<Sale>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<Sale>?>> = MutableLiveData()
        val call = RetrofitConfig().saleService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun search(search: String): MutableLiveData<ApiResponse<MutableList<Sale>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<Sale>?>> = MutableLiveData()
        val call = RetrofitConfig().saleService().search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<Sale?>> {
        val apiResponse = MutableLiveData<ApiResponse<Sale?>>()
        val call = RetrofitConfig().saleService().insert(companyName, json)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>> {
        val apiResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
        val call = RetrofitConfig().saleService().update(companyName, json)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun delete(id: String,
               firebaseToken: String,
               success: () -> Unit,
               failure: (messageError: String?) -> Unit) {
        val call = RetrofitConfig().saleService().delete(id, companyName, firebaseToken)
        call.responseHandle({
            success()
        }, {
            failure(it)
        })
    }
    
}