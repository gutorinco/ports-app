package br.com.suitesistemas.portsmobile.service.crm

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.CRM
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.service.ListService

class CRMRepository(private val companyName: String) : ListService<CRM> {

    override fun findAll(): MutableLiveData<ApiResponse<MutableList<CRM>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<CRM>?>> = MutableLiveData()
        val call = RetrofitConfig().crmService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun search(search: String): MutableLiveData<ApiResponse<MutableList<CRM>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<CRM>?>> = MutableLiveData()
        val call = RetrofitConfig().crmService().search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<CRM?>> {
        val apiResponse = MutableLiveData<ApiResponse<CRM?>>()
        val call = RetrofitConfig().crmService().insert(companyName, json)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>> {
        val apiResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
        val call = RetrofitConfig().crmService().update(companyName, json)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun delete(id: String,
        firebaseToken: String,
        success: () -> Unit,
        failure: (messageError: String?) -> Unit) {
        val call = RetrofitConfig().crmService().delete(id, companyName, firebaseToken)
        call.responseHandle({
            success()
        }, {
            failure(it)
        })
    }

}