package br.com.suitesistemas.portsmobile.service.product

import br.com.suitesistemas.portsmobile.entity.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @GET("produto/{apelido}")
    fun search(@Path("apelido") apelido: String,
               @Query("search") search: String,
               @Query("field") field: String = "Nome"): Call<MutableList<Product>>

}