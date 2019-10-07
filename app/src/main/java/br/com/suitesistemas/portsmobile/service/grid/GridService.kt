package br.com.suitesistemas.portsmobile.service.grid

import br.com.suitesistemas.portsmobile.entity.Grid
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GridService {

    @GET("grade/{apelido}")
    fun findAll(@Path("apelido") apelido: String): Call<MutableList<Grid>>

}