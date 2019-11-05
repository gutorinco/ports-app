package br.com.suitesistemas.portsmobile.service.customer

import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.entity.Customer
import retrofit2.Call
import retrofit2.http.*

interface CustomerService {

    @GET("pessoa/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("limite") limit: Int = 100): Call<MutableList<Customer>>

    @GET("pessoa/{apelido}")
    fun search(@Path("apelido") apelido: String,
                @Query("search") search: String,
                @Query("type")   type: String,
                @Query("limite") limit: Int = 100): Call<MutableList<Customer>>

    @POST("pessoa/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<Customer>

    @PUT("pessoa/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<VersionResponse>

    @DELETE("pessoa/{id}/{apelido}")
    fun delete(@Path("id") id: String, @Path("apelido") apelido: String, @Query("token") token: String): Call<Unit>

}