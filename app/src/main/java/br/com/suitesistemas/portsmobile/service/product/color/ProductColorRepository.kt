package br.com.suitesistemas.portsmobile.service.product.color

import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.custom.retrofit.responseHandle
import br.com.suitesistemas.portsmobile.entity.ProductColor

class ProductColorRepository(private val companyName: String) {

    fun findBy(productId: Int,
               success: (customers: List<ProductColor>?) -> Unit,
               failure: (messageError: String?) -> Unit,
               finished: () -> Unit) {
        val call = RetrofitConfig().productColorService().find(companyName, productId)
        call.responseHandle(200, {
            success(it)
        }, {
            failure(it)
        }, {
            finished()
        })
    }

}