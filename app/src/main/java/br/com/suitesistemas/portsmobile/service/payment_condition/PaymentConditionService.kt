package br.com.suitesistemas.portsmobile.service.payment_condition

import br.com.suitesistemas.portsmobile.model.entity.PaymentCondition
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PaymentConditionService {

    @GET("condicao/{apelido}")
    fun findAll(@Path("apelido") apelido: String): Call<MutableList<PaymentCondition>>

}