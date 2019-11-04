package br.com.suitesistemas.portsmobile.service.order

import br.com.suitesistemas.portsmobile.entity.Order
import br.com.suitesistemas.portsmobile.model.VersionResponse
import retrofit2.Call
import retrofit2.http.*

interface OrderService {

    @GET("pedido/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("limite") limit: Int = 100): Call<MutableList<Order>>

    @GET("pedido/{apelido}")
    fun search(@Path("apelido") apelido: String,
               @Query("search") search: String,
               @Query("field")  field: String = "Data",
               @Query("limite") limit: Int = 100): Call<MutableList<Order>>

    @POST("pedido/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<Order>

    @PUT("pedido/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<VersionResponse>

    @DELETE("pedido/{id}/{apelido}")
    fun delete(@Path("id") id: String, @Path("apelido") apelido: String, @Query("token") token: String): Call<Unit>

}