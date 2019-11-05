package br.com.suitesistemas.portsmobile.service.company

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Company

class CompanyRepository(private val companyName: String) {

    fun findAll(): MutableLiveData<ApiResponse<MutableList<Company>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Company>?>>()
        val call = RetrofitConfig().companyService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }
    
}