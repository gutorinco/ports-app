package br.com.suitesistemas.portsmobile.service.model_combination

import br.com.suitesistemas.portsmobile.entity.ModelCombination
import retrofit2.Call
import retrofit2.http.*

interface ModelCombinationService {

    @GET("modelo_comb/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("modelo") codModelo: String): Call<MutableList<ModelCombination>>

    @POST("modelo_comb/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body modelCombinations: MutableList<ModelCombination>): Call<MutableList<ModelCombination>>

    @DELETE("modelo_comb/{cod_modelo}/{cod_combinacao}/{apelido}")
    fun delete(@Path("cod_modelo") cod_modelo: String,
               @Path("cod_combinacao") cod_combinacao: Int,
               @Path("apelido") apelido: String): Call<Unit>

}