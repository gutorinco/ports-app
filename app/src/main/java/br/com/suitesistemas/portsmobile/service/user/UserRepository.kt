package br.com.suitesistemas.portsmobile.service.user

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.User

class UserRepository(private val companyName: String) {

    fun find(userId: Int): MutableLiveData<ApiResponse<User?>> {
        val apiResponse = MutableLiveData<ApiResponse<User?>>()
        val call = RetrofitConfig().userService().findAll(companyName, userId)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

}