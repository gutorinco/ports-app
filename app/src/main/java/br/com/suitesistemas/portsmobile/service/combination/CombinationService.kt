package br.com.suitesistemas.portsmobile.service.combination

import br.com.suitesistemas.portsmobile.model.entity.Combination
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CombinationService {

    @GET("combinacao/{apelido}")
    fun findAll(@Path("apelido") apelido: String): Call<MutableList<Combination>>

    @GET("combinacao/{apelido}")
    fun search(@Path("apelido") apelido: String, @Query("search") search: String): Call<MutableList<Combination>>

}