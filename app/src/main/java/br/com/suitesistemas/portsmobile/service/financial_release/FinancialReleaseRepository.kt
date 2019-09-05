package br.com.suitesistemas.portsmobile.service.financial_release

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.model.ApiResponse

class FinancialReleaseRepository(private val companyName: String) {

    fun findAll(): MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>>()
        val call = RetrofitConfig().financialReleaseService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun search(search: String): MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>>()
        val call = RetrofitConfig().financialReleaseService().search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

}