package br.com.suitesistemas.portsmobile.service.sale.item

import br.com.suitesistemas.portsmobile.entity.SaleItem
import retrofit2.Call
import retrofit2.http.*

interface SaleItemService {

    @GET("venda_item/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("venda") saleId: String): Call<MutableList<SaleItem>>

    @POST("venda_item/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body items: MutableList<SaleItem>): Call<MutableList<SaleItem>>

    @PUT("venda_item/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body items: MutableList<SaleItem>): Call<Any>

    @PUT("venda_item/{apelido}/delete")
    fun delete(@Path("apelido") apelido: String, @Body items: MutableList<SaleItem>): Call<Any>

}