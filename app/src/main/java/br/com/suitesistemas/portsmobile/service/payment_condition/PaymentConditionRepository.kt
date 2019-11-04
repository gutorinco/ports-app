package br.com.suitesistemas.portsmobile.service.payment_condition

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.entity.PaymentCondition
import br.com.suitesistemas.portsmobile.model.ApiResponse

class PaymentConditionRepository(private val companyName: String) {

    fun findAll(): MutableLiveData<ApiResponse<MutableList<PaymentCondition>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<PaymentCondition>?>>()
        val call = RetrofitConfig().paymentConditionService().findAll(companyName)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

}