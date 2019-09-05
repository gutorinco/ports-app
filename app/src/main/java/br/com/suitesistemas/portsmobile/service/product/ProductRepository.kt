package br.com.suitesistemas.portsmobile.service.product

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.ApiResponse

class ProductRepository(private val companyName: String) {

    fun search(search: String): MutableLiveData<ApiResponse<MutableList<Product>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<Product>?>> = MutableLiveData()
        val call = RetrofitConfig().productService().search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

}