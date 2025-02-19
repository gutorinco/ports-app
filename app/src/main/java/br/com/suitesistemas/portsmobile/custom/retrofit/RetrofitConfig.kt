package br.com.suitesistemas.portsmobile.custom.retrofit

import br.com.suitesistemas.portsmobile.BuildConfig
import br.com.suitesistemas.portsmobile.service.auth.AuthService
import br.com.suitesistemas.portsmobile.service.color.ColorService
import br.com.suitesistemas.portsmobile.service.combination.CombinationService
import br.com.suitesistemas.portsmobile.service.company.CompanyService
import br.com.suitesistemas.portsmobile.service.configuration.ConfigurationService
import br.com.suitesistemas.portsmobile.service.crm.CRMService
import br.com.suitesistemas.portsmobile.service.customer.CustomerService
import br.com.suitesistemas.portsmobile.service.financial_release.FinancialReleaseService
import br.com.suitesistemas.portsmobile.service.grid.GridService
import br.com.suitesistemas.portsmobile.service.grid.item.GridItemService
import br.com.suitesistemas.portsmobile.service.model.ModelService
import br.com.suitesistemas.portsmobile.service.model_combination.ModelCombinationService
import br.com.suitesistemas.portsmobile.service.model_grid_combination.ModelGridCombinationService
import br.com.suitesistemas.portsmobile.service.order.OrderService
import br.com.suitesistemas.portsmobile.service.order.gridItem.OrderGridItemService
import br.com.suitesistemas.portsmobile.service.order.item.OrderItemService
import br.com.suitesistemas.portsmobile.service.payment_condition.PaymentConditionService
import br.com.suitesistemas.portsmobile.service.product.ProductService
import br.com.suitesistemas.portsmobile.service.product_color.ProductColorService
import br.com.suitesistemas.portsmobile.service.sale.SaleService
import br.com.suitesistemas.portsmobile.service.sale.item.SaleItemService
import br.com.suitesistemas.portsmobile.service.unit_measure.UnitMeasureService
import br.com.suitesistemas.portsmobile.service.user.UserService
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
    fun colorService() = retrofit.create(ColorService::class.java)
    fun combinationService() = retrofit.create(CombinationService::class.java)
    fun companyService() = retrofit.create(CompanyService::class.java)
    fun configurationService() = retrofit.create(ConfigurationService::class.java)
    fun crmService() = retrofit.create(CRMService::class.java)
    fun financialReleaseService() = retrofit.create(FinancialReleaseService::class.java)
    fun gridService() = retrofit.create(GridService::class.java)
    fun gridItemService() = retrofit.create(GridItemService::class.java)
    fun modelService() = retrofit.create(ModelService::class.java)
    fun modelCombinationService() = retrofit.create(ModelCombinationService::class.java)
    fun modelGridCombinationService() = retrofit.create(ModelGridCombinationService::class.java)
    fun paymentConditionService() = retrofit.create(PaymentConditionService::class.java)
    fun productService() = retrofit.create(ProductService::class.java)
    fun productColorService() = retrofit.create(ProductColorService::class.java)
    fun orderService() = retrofit.create(OrderService::class.java)
    fun orderItemService() = retrofit.create(OrderItemService::class.java)
    fun orderGridItemService() = retrofit.create(OrderGridItemService::class.java)
    fun saleService() = retrofit.create(SaleService::class.java)
    fun saleItemService() = retrofit.create(SaleItemService::class.java)
    fun unitMeasureService() = retrofit.create(UnitMeasureService::class.java)
    fun userService() = retrofit.create(UserService::class.java)

}