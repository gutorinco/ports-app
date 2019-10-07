package br.com.suitesistemas.portsmobile.service.financial_release

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.service.SearchService

class FinancialReleaseRepository(private val companyName: String): SearchService<FinancialRelease> {

    fun findAll(): MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>>()
        val call = RetrofitConfig().financialReleaseService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun search(search: String): MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<FinancialRelease>?>>()
        val call = RetrofitConfig().financialReleaseService().search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun delete(id: String, firebaseToken: String, success: () -> Unit,failure: (messageError: String?) -> Unit) {
        TODO("not implemented")
    }
    override fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<FinancialRelease?>> {
        TODO("not implemented")
    }

}