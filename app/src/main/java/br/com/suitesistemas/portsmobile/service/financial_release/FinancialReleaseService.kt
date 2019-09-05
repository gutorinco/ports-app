package br.com.suitesistemas.portsmobile.service.financial_release

import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FinancialReleaseService {

    @GET("lancamento/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("limite") limit: Int = 100): Call<MutableList<FinancialRelease>>

    @GET("lancamento/{apelido}")
    fun search(@Path("apelido") apelido: String,
               @Query("search") search: String,
               @Query("limite") limit: Int = 100): Call<MutableList<FinancialRelease>>

}