package br.com.suitesistemas.portsmobile.service.configuration

import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.entity.Configuration
import retrofit2.Call
import retrofit2.http.*

interface ConfigurationService {

    @GET("configuracao/{apelido}")
    fun findAll(@Path("apelido") apelido: String): Call<MutableList<Configuration>>

    @POST("configuracao/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body config: Configuration): Call<Configuration>

    @PUT("configuracao/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body config: Configuration): Call<VersionResponse>

    @DELETE("configuracao/{id}/{apelido}")
    fun delete(@Path("id") id: Integer, @Path("apelido") apelido: String): Call<Unit>

}