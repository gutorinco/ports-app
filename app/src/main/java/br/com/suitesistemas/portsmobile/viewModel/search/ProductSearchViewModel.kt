package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.service.product.ProductRepository
import br.com.suitesistemas.portsmobile.service.product_color.ProductColorRepository

class ProductSearchViewModel : SelectSearchViewModel<Product>() {

    var productColorResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorRollbackResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    val removedProductColors: MutableList<ProductColor> = mutableListOf()
    private lateinit var productColorRepository: ProductColorRepository

    override fun initRepository(company: String) {
        companyName = company
        repository = ProductRepository(company)
        productColorRepository = ProductColorRepository(company)
    }

    fun fetchAllColorsBy(position: Int) {
        val product = getBy(position)
        deletedOnSearch = true
        productColorResponse = productColorRepository.findBy(product.num_codigo_online)
    }

    fun addRemovedProductColors(colors: MutableList<ProductColor>) {
        removedProductColors.clear()
        removedProductColors.addAll(colors)
    }

    fun productColorsDeleteRollback() {
        removedProductColors.forEach { it.cod_produto = removedObject!! }
        productColorRollbackResponse = productColorRepository.insert(removedProductColors)
    }


}