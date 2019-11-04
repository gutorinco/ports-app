package br.com.suitesistemas.portsmobile.service.unit_measure

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.UnitMeasure
import br.com.suitesistemas.portsmobile.model.ApiResponse

class UnitMeasureRepository(private val companyName: String) {

    private val service = RetrofitConfig().unitMeasureService()

    fun findAll(): MutableLiveData<ApiResponse<MutableList<UnitMeasure>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<UnitMeasure>?>>()
        val call = service.findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    fun search(search: String): MutableLiveData<ApiResponse<MutableList<UnitMeasure>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<UnitMeasure>?>>()
        val call = service.search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

}