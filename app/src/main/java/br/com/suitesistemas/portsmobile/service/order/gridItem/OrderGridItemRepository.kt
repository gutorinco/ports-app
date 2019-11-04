package br.com.suitesistemas.portsmobile.service.order.gridItem

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.OrderGridItem
import br.com.suitesistemas.portsmobile.model.ApiResponse

class OrderGridItemRepository(private val companyName: String) {

    fun findAll(orderId: String): MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()
        val call = RetrofitConfig().orderGridItemService().findAll(companyName, orderId)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun insert(items: MutableList<OrderGridItem>): MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()
        val call = RetrofitConfig().orderGridItemService().insert(companyName, items)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun update(items: MutableList<OrderGridItem>): MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()
        val call = RetrofitConfig().orderGridItemService().update(companyName, items)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse

    }

    fun delete(id: String): MutableLiveData<ApiResponse<Boolean?>> {
        val apiResponse = MutableLiveData<ApiResponse<Boolean?>>()
        val call = RetrofitConfig().orderGridItemService().delete(id, companyName)

        call.responseHandle(204) {
            apiResponse.value = ApiResponse(true)
        }

        return apiResponse
    }

}