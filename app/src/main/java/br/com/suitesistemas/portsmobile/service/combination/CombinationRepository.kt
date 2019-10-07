package br.com.suitesistemas.portsmobile.service.combination

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.service.SearchService

class CombinationRepository(private val companyName: String) : SearchService<Combination> {

    private val service = RetrofitConfig().combinationService()

    fun findAll(): MutableLiveData<ApiResponse<MutableList<Combination>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Combination>?>>()
        val call = service.findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun search(search: String): MutableLiveData<ApiResponse<MutableList<Combination>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<Combination>?>>()
        val call = service.search(companyName, search)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

    override fun delete(id: String, firebaseToken: String, success: () -> Unit, failure: (messageError: String?) -> Unit) {
        TODO("not implemented")
    }

    override fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<Combination?>> {
        TODO("not implemented")
    }

}