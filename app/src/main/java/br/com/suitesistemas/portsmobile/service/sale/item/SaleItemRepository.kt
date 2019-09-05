package br.com.suitesistemas.portsmobile.service.sale.item

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.SaleItem
import br.com.suitesistemas.portsmobile.model.ApiResponse

class SaleItemRepository(private val companyName: String) {

    fun findAll(saleId: String): MutableLiveData<ApiResponse<MutableList<SaleItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
        val call = RetrofitConfig().saleItemService().findAll(companyName, saleId)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun insert(items: MutableList<SaleItem>): MutableLiveData<ApiResponse<MutableList<SaleItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
        val call = RetrofitConfig().saleItemService().insert(companyName, items)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun update(items: MutableList<SaleItem>): MutableLiveData<ApiResponse<Any?>> {
        val apiResponse = MutableLiveData<ApiResponse<Any?>>()
        val call = RetrofitConfig().saleItemService().update(companyName, items)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse

    }

    fun delete(items: MutableList<SaleItem>): MutableLiveData<ApiResponse<Any?>> {
        val apiResponse = MutableLiveData<ApiResponse<Any?>>()
        val call = RetrofitConfig().saleItemService().delete(companyName, items)

        call.responseHandle(204) {
            apiResponse.value = it
        }

        return apiResponse
    }
    
}