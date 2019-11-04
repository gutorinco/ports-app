package br.com.suitesistemas.portsmobile.service.order

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.Order
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.service.ListService

class OrderRepository(private val companyName: String) : ListService<Order> {

    override fun findAll(): MutableLiveData<ApiResponse<MutableList<Order>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<Order>?>> = MutableLiveData()
        val call = RetrofitConfig().orderService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun search(search: String): MutableLiveData<ApiResponse<MutableList<Order>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<Order>?>> = MutableLiveData()
        val call = RetrofitConfig().orderService().search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<Order?>> {
        val apiResponse = MutableLiveData<ApiResponse<Order?>>()
        val call = RetrofitConfig().orderService().insert(companyName, json)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>> {
        val apiResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
        val call = RetrofitConfig().orderService().update(companyName, json)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun delete(id: String,
                        firebaseToken: String,
                        success: () -> Unit,
                        failure: (messageError: String?) -> Unit) {
        val call = RetrofitConfig().orderService().delete(id, companyName, firebaseToken)
        call.responseHandle({
            success()
        }, {
            failure(it)
        })
    }

}