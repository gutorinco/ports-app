package br.com.suitesistemas.portsmobile.service.model_grid_combination

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.ModelGridCombination

class ModelGridCombinationRepository(private val companyName: String) {

    private val service = RetrofitConfig().modelGridCombinationService()

    fun findBy(modelId: String): MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>>()
        val call = service.findAll(companyName, modelId)

        call.responseHandle(200) { apiResponse.value = it }

        return apiResponse
    }

    fun insert(modelCombinations: MutableList<ModelGridCombination>): MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>>()
        val call = service.insert(companyName, modelCombinations)

        call.responseHandle(201) { apiResponse.value = it }

        return apiResponse
    }

    fun delete(modelId: String, combinationId: Int): MutableLiveData<ApiResponse<Boolean?>> {
        val apiResponse = MutableLiveData<ApiResponse<Boolean?>>()
        val call = service.delete(modelId, combinationId, companyName)

        call.responseHandle(204) {
            apiResponse.value = ApiResponse(true)
        }

        return apiResponse
    }

}