package br.com.suitesistemas.portsmobile.service.auth

import br.com.suitesistemas.portsmobile.model.UserRequest
import br.com.suitesistemas.portsmobile.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth")
    fun signIn(@Body user: UserRequest): Call<UserResponse>

}