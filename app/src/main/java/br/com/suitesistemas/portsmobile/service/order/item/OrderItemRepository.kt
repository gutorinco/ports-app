package br.com.suitesistemas.portsmobile.service.order.item

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.OrderItem

class OrderItemRepository(private val companyName: String) {

    fun findAll(orderId: String): MutableLiveData<ApiResponse<MutableList<OrderItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
        val call = RetrofitConfig().orderItemService().findAll(companyName, orderId)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun insert(items: MutableList<OrderItem>): MutableLiveData<ApiResponse<MutableList<OrderItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
        val call = RetrofitConfig().orderItemService().insert(companyName, items)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun update(items: MutableList<OrderItem>): MutableLiveData<ApiResponse<MutableList<OrderItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
        val call = RetrofitConfig().orderItemService().update(companyName, items)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse

    }

    fun delete(id: String, sequence: Int): MutableLiveData<ApiResponse<Boolean?>> {
        val apiResponse = MutableLiveData<ApiResponse<Boolean?>>()
        val call = RetrofitConfig().orderItemService().delete(id, sequence, companyName)

        call.responseHandle(204) {
            apiResponse.value = ApiResponse(true)
        }

        return apiResponse
    }

}