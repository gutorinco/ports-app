package br.com.suitesistemas.portsmobile.service.model

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.entity.Model
import br.com.suitesistemas.portsmobile.service.ListService

class ModelRepository(private val companyName: String) : ListService<Model> {

    private val service = RetrofitConfig().modelService()

    override fun findAll(): MutableLiveData<ApiResponse<MutableList<Model>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Model>?>>()
        val call = service.findAll(companyName)

        call.responseHandle(200) { apiResponse.value = it }

        return apiResponse
    }

    override fun search(search: String): MutableLiveData<ApiResponse<MutableList<Model>?>> {
        val apiResponse: MutableLiveData<ApiResponse<MutableList<Model>?>> = MutableLiveData()
        val call = service.search(companyName, search)

        call.responseHandle(200) { apiResponse.value = it }

        return apiResponse
    }

    override fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<Model?>> {
        val apiResponse = MutableLiveData<ApiResponse<Model?>>()
        val call = service.insert(companyName, json)

        call.responseHandle(201) { apiResponse.value = it }

        return apiResponse
    }

    override fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>> {
        val apiResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
        val call = service.update(companyName, json)

        call.responseHandle(200) { apiResponse.value = it }

        return apiResponse
    }

    override fun delete(id: String, firebaseToken: String, success: () -> Unit, failure: (messageError: String?) -> Unit) {
        val call = service.delete(id, companyName, firebaseToken)
        call.responseHandle({
            success()
        }, {
            failure(it)
        })
    }

    fun delete(id: String, firebaseToken: String): MutableLiveData<ApiResponse<Boolean?>> {
        val apiResponse = MutableLiveData<ApiResponse<Boolean?>>()
        val call = service.delete(id, companyName, firebaseToken)

        call.responseHandle(204) {
            apiResponse.value = ApiResponse(true)
        }

        return apiResponse
    }

}