package br.com.suitesistemas.portsmobile.service.product_color

import br.com.suitesistemas.portsmobile.entity.ProductColor
import retrofit2.Call
import retrofit2.http.*

interface ProductColorService {

    @GET("produto_cor/{apelido}")
    fun find(@Path("apelido") apelido: String,
             @Query("produto") productId: String): Call<MutableList<ProductColor>>

    @POST("produto_cor/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body json: MutableList<ProductColor>): Call<MutableList<ProductColor>>

    @DELETE("produto_cor/{cod_cor}/{cod_produto}/{apelido}")
    fun delete(@Path("cod_cor") cod_cor: String,
               @Path("cod_produto") cod_produto: String,
               @Path("apelido") apelido: String): Call<Unit>

}