package br.com.suitesistemas.portsmobile.service.auth

import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.model.UserRequest
import br.com.suitesistemas.portsmobile.model.UserResponse

class AuthRepository {

    fun signIn(user: UserRequest,
               success: (userResponse: UserResponse?) -> Unit,
               failure: (messageError: String?) -> Unit,
               finished: () -> Unit) {
        val call = RetrofitConfig().authService().signIn(user)
        call.responseHandle(200, {
            success(it)
        }, {
            failure(it)
        }, {
            finished()
        })
    }
}