package br.com.suitesistemas.portsmobile.service.company

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.Company
import br.com.suitesistemas.portsmobile.model.ApiResponse

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