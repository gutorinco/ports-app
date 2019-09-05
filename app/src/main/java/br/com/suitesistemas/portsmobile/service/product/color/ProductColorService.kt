package br.com.suitesistemas.portsmobile.service.product.color

import br.com.suitesistemas.portsmobile.entity.ProductColor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductColorService {

    @GET("produto_cor/{apelido}")
    fun find(@Path("apelido") apelido: String,
             @Query("produto") productId: Int): Call<List<ProductColor>>

}