package br.com.suitesistemas.portsmobile.service.product

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.service.SearchService

class ProductRepository(private val companyName: String) : SearchService<Product> {

    private val service = RetrofitConfig().productService()

    fun findAll(): MutableLiveData<ApiResponse<MutableList<Product>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Product>?>>()
        val call = service.findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun search(search: String): MutableLiveData<ApiResponse<MutableList<Product>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<Product>?>> = MutableLiveData()
        val call = service.search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<Product?>> {
        val apiResponse = MutableLiveData<ApiResponse<Product?>>()
        val call = service.insert(companyName, json)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>> {
        val apiResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
        val call = service.update(companyName, json)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun delete(id: String, firebaseToken: String, success: () -> Unit, failure: (messageError: String?) -> Unit) {
        val call = service.delete(id, companyName, firebaseToken)
        call.responseHandle({
            success()
        }, {
            failure(it)
        })
    }

}