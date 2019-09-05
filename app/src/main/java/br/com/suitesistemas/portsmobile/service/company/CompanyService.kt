package br.com.suitesistemas.portsmobile.service.company

import br.com.suitesistemas.portsmobile.entity.Company
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CompanyService {

    @GET("empresa/{apelido}")
    fun findAll(@Path("apelido") apelido: String): Call<MutableList<Company>>

}