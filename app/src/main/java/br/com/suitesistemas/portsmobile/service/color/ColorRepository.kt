package br.com.suitesistemas.portsmobile.service.color

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse

class ColorRepository(private val companyName: String) {

    private val service = RetrofitConfig().colorService()

    fun findAll(): MutableLiveData<ApiResponse<MutableList<Color>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Color>?>>()
        val call = service.findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun search(search: String): MutableLiveData<ApiResponse<MutableList<Color>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Color>?>>()
        val call = service.search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<Color?>> {
        val apiResponse = MutableLiveData<ApiResponse<Color?>>()
        val call = service.insert(companyName, json)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>> {
        val apiResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
        val call = service.update(companyName, json)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun delete(id: String, firebaseToken: String, success: () -> Unit, failure: (messageError: String?) -> Unit) {
        val call = service.delete(id, companyName, firebaseToken)
        call.responseHandle({
            success()
        }, {
            failure(it)
        })
    }

}