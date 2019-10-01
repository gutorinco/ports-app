package br.com.suitesistemas.portsmobile.service.color

import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.model.VersionResponse
import retrofit2.Call
import retrofit2.http.*

interface ColorService {

    @GET("cor/{apelido}")
    fun findAll(@Path("apelido") apelido: String, @Query("limite") limit: Int = 100): Call<MutableList<Color>>

    @GET("cor/{apelido}")
    fun search(@Path("apelido") apelido: String,
               @Query("search") search: String,
               @Query("limite") limit: Int = 100): Call<MutableList<Color>>

    @POST("cor/{apelido}")
    fun insert(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<Color>

    @PUT("cor/{apelido}")
    fun update(@Path("apelido") apelido: String, @Body json: MutableList<HashMap<String, Any?>>): Call<VersionResponse>

    @DELETE("cor/{id}/{apelido}")
    fun delete(@Path("id") id: String, @Path("apelido") apelido: String, @Query("token") token: String): Call<Unit>

}