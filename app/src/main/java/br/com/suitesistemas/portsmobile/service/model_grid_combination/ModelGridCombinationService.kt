package br.com.suitesistemas.portsmobile.service.model_grid_combination

import br.com.suitesistemas.portsmobile.entity.ModelGridCombination
import retrofit2.Call
import retrofit2.http.*

interface ModelGridCombinationService {

    @GET("modelo_comb_grade/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("modelo") codModelo: String): Call<MutableList<ModelGridCombination>>

    @POST("modelo_comb_grade/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body modelCombinations: MutableList<ModelGridCombination>): Call<MutableList<ModelGridCombination>>

    @DELETE("modelo_comb_grade/{cod_modelo}/{cod_combinacao}/{apelido}")
    fun delete(@Path("cod_modelo") cod_modelo: String,
               @Path("cod_combinacao") cod_combinacao: Int,
               @Path("apelido") apelido: String): Call<Unit>

}