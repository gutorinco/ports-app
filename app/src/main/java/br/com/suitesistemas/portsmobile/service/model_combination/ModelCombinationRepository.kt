package br.com.suitesistemas.portsmobile.service.model_combination

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.ModelCombination
import br.com.suitesistemas.portsmobile.model.ApiResponse

class ModelCombinationRepository(private val companyName: String) {

    private val service = RetrofitConfig().modelCombinationService()

    fun findBy(modelId: String): MutableLiveData<ApiResponse<MutableList<ModelCombination>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
        val call = service.findAll(companyName, modelId)

        call.responseHandle(200) { apiResponse.value = it }

        return apiResponse
    }

    fun insert(modelCombinations: MutableList<ModelCombination>): MutableLiveData<ApiResponse<MutableList<ModelCombination>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
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