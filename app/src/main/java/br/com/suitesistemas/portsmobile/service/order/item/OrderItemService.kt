package br.com.suitesistemas.portsmobile.service.order.item

import br.com.suitesistemas.portsmobile.entity.OrderItem
import retrofit2.Call
import retrofit2.http.*

interface OrderItemService {

    @GET("pedido_item/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("pedido") saleId: String): Call<MutableList<OrderItem>>

    @POST("pedido_item/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body items: MutableList<OrderItem>): Call<MutableList<OrderItem>>

    @PUT("pedido_item/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body items: MutableList<OrderItem>): Call<MutableList<OrderItem>>

    @DELETE("pedido_item/{id}/{sequencia}/{apelido}")
    fun delete(@Path("id") id: String,
               @Path("sequencia") sequencia: Int,
               @Path("apelido") apelido: String): Call<Any>

}