package br.com.suitesistemas.portsmobile.service.unit_measure

import br.com.suitesistemas.portsmobile.entity.UnitMeasure
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnitMeasureService {

    @GET("unidade/{apelido}")
    fun findAll(@Path("apelido") apelido: String): Call<MutableList<UnitMeasure>>

    @GET("unidade/{apelido}")
    fun search(@Path("apelido") apelido: String, @Query("search") search: String): Call<MutableList<UnitMeasure>>

}