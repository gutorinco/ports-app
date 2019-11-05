package br.com.suitesistemas.portsmobile.service.product

import br.com.suitesistemas.portsmobile.model.CodeResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.entity.Product
import retrofit2.Call
import retrofit2.http.*

interface ProductService {

    @GET("produto/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("limite") limit: Int = 100): Call<MutableList<Product>>

    @GET("produto/{apelido}")
    fun search(@Path("apelido") apelido: String,
               @Query("search") search: String,
               @Query("field") field: String): Call<MutableList<Product>>

    @GET("produto/{apelido}/auto_increment")
    fun getNextCode(@Path("apelido") apelido: String): Call<CodeResponse>

    @POST("produto/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<Product>

    @PUT("produto/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<VersionResponse>

    @DELETE("produto/{id}/{apelido}")
    fun delete(@Path("id") id: String, @Path("apelido") apelido: String, @Query("token") token: String): Call<Unit>

}