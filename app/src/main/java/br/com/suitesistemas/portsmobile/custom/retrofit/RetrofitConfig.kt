package br.com.suitesistemas.portsmobile.custom.retrofit

import br.com.suitesistemas.portsmobile.BuildConfig
import br.com.suitesistemas.portsmobile.service.auth.AuthService
import br.com.suitesistemas.portsmobile.service.company.CompanyService
import br.com.suitesistemas.portsmobile.service.customer.CustomerService
import br.com.suitesistemas.portsmobile.service.financial_release.FinancialReleaseService
import br.com.suitesistemas.portsmobile.service.payment_condition.PaymentConditionService
import br.com.suitesistemas.portsmobile.service.product.ProductService
import br.com.suitesistemas.portsmobile.service.product.color.ProductColorService
import br.com.suitesistemas.portsmobile.service.sale.SaleService
import br.com.suitesistemas.portsmobile.service.sale.item.SaleItemService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConfig {

    private val httpClient = OkHttpClient().newBuilder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

    private val retrofit = Retrofit.Builder()
           .baseUrl(BuildConfig.BASE_URL)
           .addConverterFactory(JacksonConverterFactory.create())
           .client(httpClient)
           .build()

    fun authService() = retrofit.create(AuthService::class.java)
    fun customerService() = retrofit.create(CustomerService::class.java)
    fun companyService() = retrofit.create(CompanyService::class.java)
    fun financialReleaseService() = retrofit.create(FinancialReleaseService::class.java)
    fun paymentConditionService() = retrofit.create(PaymentConditionService::class.java)
    fun productService() = retrofit.create(ProductService::class.java)
    fun productColorService() = retrofit.create(ProductColorService::class.java)
    fun saleService() = retrofit.create(SaleService::class.java)
    fun saleItemService() = retrofit.create(SaleItemService::class.java)

}