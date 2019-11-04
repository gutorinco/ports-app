package br.com.suitesistemas.portsmobile.service.crm

import br.com.suitesistemas.portsmobile.entity.CRM
import br.com.suitesistemas.portsmobile.model.VersionResponse
import retrofit2.Call
import retrofit2.http.*

interface CRMService {

    @GET("crm/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("limite") limit: Int = 100): Call<MutableList<CRM>>

    @GET("crm/{apelido}")
    fun search(@Path("apelido") apelido: String,
        @Query("search") search: String,
        @Query("field")  field: String = "Data Cadastro",
        @Query("limite") limit: Int = 100): Call<MutableList<CRM>>

    @POST("crm/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<CRM>

    @PUT("crm/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<VersionResponse>

    @DELETE("crm/{id}/{apelido}")
    fun delete(@Path("id") id: String, @Path("apelido") apelido: String, @Query("token") token: String): Call<Unit>

}