package br.com.suitesistemas.portsmobile.service.user

import br.com.suitesistemas.portsmobile.model.entity.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {

    @GET("usuario/{apelido}/{codigo}")
    fun findAll(@Path("apelido") apelido: String, @Path("codigo") codigo: Int): Call<User>

}