package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.product.ProductRepository
import br.com.suitesistemas.portsmobile.service.product_color.ProductColorRepository

class ProductViewModel : ListViewModel<Product>() {

    private lateinit var repository: ProductRepository
    private lateinit var productColorRepository: ProductColorRepository
    var productColorResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorRollbackResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    val removedProductColors: MutableList<ProductColor> = mutableListOf()

    fun fetchAllProducts(companyName: String) {
        this.companyName = companyName
        repository = ProductRepository(companyName)
        productColorRepository = ProductColorRepository(companyName)
        response = repository.findAll()
    }

    fun refresh() {
        list.clear()
        refreshResponse = repository.findAll()
    }

    fun updateList(productResponse: Product) {
        for (product in list)
            if (product.num_codigo_online == productResponse.num_codigo_online)
                product.copy(productResponse)
        response.value = ApiResponse(getSortingList(), EHttpOperation.PUT)
    }

    fun addRemovedProductColors(colors: MutableList<ProductColor>) {
        removedProductColors.clear()
        removedProductColors.addAll(colors)
    }

    fun fetchAllColorsBy(position: Int) {
        val product = getBy(position)
        productColorResponse = productColorRepository.findBy(product.num_codigo_online)
    }

    fun productColorsDeleteRollback() {
        removedProductColors.forEach { it.cod_produto = removedObject!! }
        productColorRollbackResponse = productColorRepository.insert(removedProductColors)
    }

    fun delete(position: Int, firebaseToken: String) {
        val product = getBy(position)
        repository.delete(product.num_codigo_online, firebaseToken, {
            removedObject = product
            removedPosition = position
            list.removeAt(position)
            response.value = ApiResponse(getSortingList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback() {
        rollbackResponse = repository.insert(getJsonRequest("produto"))
    }

    override fun sortingList() = list.sortedWith(compareBy(Product::dsc_produto))

}