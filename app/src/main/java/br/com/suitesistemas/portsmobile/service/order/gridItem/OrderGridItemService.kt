package br.com.suitesistemas.portsmobile.service.order.gridItem

import br.com.suitesistemas.portsmobile.entity.OrderGridItem
import retrofit2.Call
import retrofit2.http.*

interface OrderGridItemService {

    @GET("pedido_item_grade/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("pedido") saleId: String): Call<MutableList<OrderGridItem>>

    @POST("pedido_item_grade/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body grids: MutableList<OrderGridItem>): Call<MutableList<OrderGridItem>>

    @PUT("pedido_item_grade/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body grids: MutableList<OrderGridItem>): Call<MutableList<OrderGridItem>>

    @DELETE("pedido_item_grade/{id}/{apelido}")
    fun delete(@Path("id") id: String, @Path("apelido") apelido: String): Call<Any>

}