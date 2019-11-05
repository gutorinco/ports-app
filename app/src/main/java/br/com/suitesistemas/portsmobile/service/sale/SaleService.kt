package br.com.suitesistemas.portsmobile.service.sale

import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.entity.Sale
import retrofit2.Call
import retrofit2.http.*

interface SaleService {

    @GET("venda/{apelido}")
    fun findAll(@Path("apelido") apelido: String,
                @Query("vendedor") vendedorId: String? = null,
                @Query("limite") limit: Int = 100): Call<MutableList<Sale>>

    @GET("venda/{apelido}")
    fun search(@Path("apelido") apelido: String,
               @Query("search") search: String,
               @Query("field")  field: String = "Data",
               @Query("vendedor") vendedor: String? = null,
               @Query("limite") limit: Int = 100): Call<MutableList<Sale>>

    @POST("venda/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<Sale>

    @PUT("venda/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<VersionResponse>

    @DELETE("venda/{id}/{apelido}")
    fun delete(@Path("id") id: String, @Path("apelido") apelido: String, @Query("token") token: String): Call<Unit>

}