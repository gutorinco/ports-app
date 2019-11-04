package br.com.suitesistemas.portsmobile.service.grid

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.Grid
import br.com.suitesistemas.portsmobile.model.ApiResponse

class GridRepository(private val companyName: String) {

    fun findAll(): MutableLiveData<ApiResponse<MutableList<Grid>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Grid>?>>()
        val call = RetrofitConfig().gridService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

}