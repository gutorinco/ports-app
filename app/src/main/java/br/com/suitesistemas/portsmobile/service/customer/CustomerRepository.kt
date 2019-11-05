package br.com.suitesistemas.portsmobile.service.customer

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.entity.Customer
import br.com.suitesistemas.portsmobile.service.ListService

class CustomerRepository(private val companyName: String) : ListService<Customer> {

    private val service = RetrofitConfig().customerService()

    override fun findAll(): MutableLiveData<ApiResponse<MutableList<Customer>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Customer>?>>()
        val call = service.findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun search(search: String, type: String): MutableLiveData<ApiResponse<MutableList<Customer>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Customer>?>>()
        val call = service.search(companyName, search, type)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<Customer?>> {
        val apiResponse = MutableLiveData<ApiResponse<Customer?>>()
        val call = service.insert(companyName, json)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>> {
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

    override fun search(search: String): MutableLiveData<ApiResponse<MutableList<Customer>?>> {
        TODO("not implemented")
    }

}