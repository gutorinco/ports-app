package br.com.suitesistemas.portsmobile.service.configuration

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.Configuration
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse

class ConfigurationRepository(private val companyName: String) {

    private val service = RetrofitConfig().configurationService()

    fun findAll(): MutableLiveData<ApiResponse<MutableList<Configuration>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Configuration>?>>()
        val call = service.findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun insert(config: Configuration): MutableLiveData<ApiResponse<Configuration?>> {
        val apiResponse = MutableLiveData<ApiResponse<Configuration?>>()
        val call = service.insert(companyName, config)

        call.responseHandle(201) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun update(config: Configuration): MutableLiveData<ApiResponse<VersionResponse?>> {
        val apiResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
        val call = service.update(companyName, config)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun delete(id: Integer, success: () -> Unit, failure: (messageError: String?) -> Unit) {
        val call = service.delete(id, companyName)
        call.responseHandle({
            success()
        }, {
            failure(it)
        })
    }

}