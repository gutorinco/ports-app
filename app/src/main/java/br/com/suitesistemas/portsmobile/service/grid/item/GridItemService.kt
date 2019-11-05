package br.com.suitesistemas.portsmobile.service.grid.item

import br.com.suitesistemas.portsmobile.model.entity.GridItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GridItemService {

    @GET("grade_item/{apelido}/{cod_grade}")
    fun findAllBy(@Path("apelido") apelido: String, @Path("cod_grade") cod_grade: Int): Call<MutableList<GridItem>>

}